package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.guardrail.InputGuardrailException;
import dev.langchain4j.guardrails.MessageModeratorInputGuardrail;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.cohere.CohereEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.mistralai.MistralAiModerationModel;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.ModerationException;
import dev.langchain4j.service.Result;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.inject.Inject;
import org.agoncal.application.vintagestore.rag.IsContentRelatedQueryRouter;
import org.agoncal.application.vintagestore.summarize.OpenAISummarizer;
import org.agoncal.application.vintagestore.summarize.SummarizingTokenWindowChatMemory;
import org.agoncal.application.vintagestore.tool.ItemsInStockTools;
import org.agoncal.application.vintagestore.tool.LegalDocumentTools;
import org.agoncal.application.vintagestore.tool.UserLoggedInTools;
import org.jboss.logging.Logger;

import static dev.langchain4j.model.anthropic.AnthropicChatModelName.CLAUDE_SONNET_4_20250514;
import static dev.langchain4j.model.mistralai.MistralAiChatModelName.MISTRAL_MODERATION_LATEST;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_MINI;

import static java.time.Duration.ofSeconds;

@WebSocket(path = "/chat")
public class VintageStoreChatBot {

  private static final Logger LOG = Logger.getLogger(VintageStoreChatBot.class);

  // AI-Model API keys from environment variable
  private static final String ANTHROPIC_API_KEY = System.getenv("ANTHROPIC_API_KEY");
  private static final String MISTRAL_AI_API_KEY = System.getenv("MISTRAL_AI_API_KEY");
  private static final String COHERE_API_KEY = System.getenv("COHERE_API_KEY");
  private static final String COHERE_EMBED_ENGLISH = "embed-english-v3.0"; // or embed-english-light-v3.0
  private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
  // Constants for Qdrant configuration
  private static final String QDRANT_COLLECTION = "VintageStore";
  private static final String QDRANT_HOST = "localhost";
  private static final int QDRANT_PORT = 6334;
  // Prompts
  private static final String WELCOME_PROMPT = "Hello, how can I help you?";
  private static final String MODERATION_PROMPT = "/!\\ I don't know why you are frustrated, but I will redirect you to a human assistant who can help you better. Please wait a moment...";
  // Other constants
  private static final int MAX_TOKENS = 32_000;
  private static final int TOKEN_LIMIT = 1_000;
  private static final String RED = "\u001B[31m";
  private static final String ORANGE = "\u001B[33m";
  private static final String GREEN = "\u001B[32m";
  private static final String RESET = "\u001B[0m";
  private static final boolean IS_LOGGING_ENABLED = true;

  private VintageStoreAssistant assistant;
  private ChatMemoryStore redisChatMemoryStore;
  private QdrantClient qdrantClient;

  @Inject
  WebSocketConnection webSocketConnection;

  @OnOpen
  public String onOpen() throws Exception {
    LOG.info("WebSocket chat connection opened with session id " + sessionId());
    assistant = initializeVintageStoreAssistant();
    return WELCOME_PROMPT;
  }

  @OnTextMessage
  public String onMessage(String message) throws Exception {
    LOG.info("Received message: " + message + " with session id " + sessionId());

    if ("CLEAR_CONVERSATION".equals(message)) {
      LOG.info("Clearing conversation history");
      redisChatMemoryStore.deleteMessages(webSocketConnection.id());
      return WELCOME_PROMPT;
    }

    try {
      long startTime = System.currentTimeMillis();
      Result<String> response = assistant.chat(webSocketConnection.id(), message);
      logInvocation(startTime, response.tokenUsage());
      return response.content();
    } catch (InputGuardrailException e) {
      Throwable cause = e.getCause();
      if (cause instanceof ModerationException) {
        LOG.warn("/!\\ The customer is not happy /!\\ " + message + " - " + ((ModerationException) cause).moderation());
        return MODERATION_PROMPT;
      } else {
        throw e;
      }
    }
  }

  @OnClose
  public void onClose() {
    LOG.info("WebSocket chat connection closed with session id " + sessionId());
    redisChatMemoryStore.deleteMessages(webSocketConnection.id());
    if (qdrantClient != null) {
      LOG.info("Closing Qdrant client connection");
      qdrantClient.close();
    }
  }

  private void logInvocation(long startTime, TokenUsage tokenUsage) {
    long duration = System.currentTimeMillis() - startTime;
    String message = "Chat response duration (" + duration + " ms)";

    if (tokenUsage != null) {
      message += " - Tokens: Input (" + tokenUsage.inputTokenCount() + "), Output (" + tokenUsage.outputTokenCount() + "), Total (" + tokenUsage.totalTokenCount() + ")";
    }

    if (duration < 6_000) {
      LOG.info(GREEN + message + RESET);
    } else if (duration <= 10_000) {
      LOG.warn(ORANGE + message + RESET);
    } else {
      LOG.error(RED + message + RESET);
    }
  }

  private String sessionId() {
    if (webSocketConnection == null) {
      return "default";
    } else {
      return webSocketConnection.id();
    }
  }

  private VintageStoreAssistant initializeVintageStoreAssistant() {

    // =============================
    // ==        AI MODELS        ==
    // =============================

    // Initialize the chat model
    ChatModel anthropicChatModel = AnthropicChatModel.builder()
      .apiKey(ANTHROPIC_API_KEY)
      .modelName(CLAUDE_SONNET_4_20250514.toString())
      .temperature(0.3)
      .timeout(ofSeconds(60))
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();

    // Initialize the moderation model
    ModerationModel mistralModerationModel = new MistralAiModerationModel.Builder()
      .apiKey(MISTRAL_AI_API_KEY)
      .modelName(MISTRAL_MODERATION_LATEST.toString())
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();

    // Initialize the embedding model
    EmbeddingModel cohereEmbeddingModel = CohereEmbeddingModel.builder()
      .apiKey(COHERE_API_KEY)
      .modelName(COHERE_EMBED_ENGLISH)
      .inputType("search_document")
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();

    // Initialize the query router model
    ChatModel ollamaQueryRouterModel = OllamaChatModel.builder()
      .baseUrl("http://localhost:11434/")
      .modelName("phi4-mini")
      .temperature(0.1)
      .timeout(ofSeconds(60))
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();


    // =============================
    // ==         MEMORY          ==
    // =============================

    redisChatMemoryStore = RedisChatMemoryStore.builder()
      .host("localhost")
      .port(6379)
      .build();

    ChatMemoryProvider redisChatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
      .id(webSocketConnection.id())
      .maxMessages(20)
      .chatMemoryStore(redisChatMemoryStore)
      .build();


    // =============================
    // ==           RAG           ==
    // =============================

    // Initialize the embedding store
    qdrantClient = new QdrantClient(QdrantGrpcClient.newBuilder(QDRANT_HOST, QDRANT_PORT, false).build());

    QdrantEmbeddingStore qdrantEmbeddingStore = QdrantEmbeddingStore.builder()
      .client(qdrantClient)
      .collectionName(QDRANT_COLLECTION)
      .build();

    ContentRetriever qdrantContentRetriever = EmbeddingStoreContentRetriever.builder()
      .embeddingStore(qdrantEmbeddingStore)
      .embeddingModel(cohereEmbeddingModel)
      .build();

    // Creating the query router
    QueryRouter queryRouter = IsContentRelatedQueryRouter.builder()
      .chatModel(ollamaQueryRouterModel)
      .contentRetriever(qdrantContentRetriever)
      .build();

    RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
      .queryRouter(queryRouter)
      .build();


    // =============================
    // ==           MCP           ==
    // =============================

    McpTransport transport = new StreamableHttpMcpTransport.Builder()
      .url("http://localhost:8780/mcp")
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();

    McpClient mcpClient = new DefaultMcpClient.Builder()
      .key("VintageStoreMCPClient")
      .transport(transport)
      .build();

    McpToolProvider mcpToolProvider = McpToolProvider.builder()
      .mcpClients(mcpClient)
      .filter((client, tool) -> tool.name().contains("converts_usd_to"))
      .build();


    // =============================
    // == VINTAGE STORE ASSISTANT ==
    // =============================

    VintageStoreAssistant assistant = AiServices.builder(VintageStoreAssistant.class)
      .chatModel(anthropicChatModel)
      .chatMemoryProvider(redisChatMemoryProvider)
      .inputGuardrails(new MessageModeratorInputGuardrail(mistralModerationModel))
      .retrievalAugmentor(retrievalAugmentor)
      .tools(new LegalDocumentTools(), new ItemsInStockTools(), new UserLoggedInTools())
      .toolProvider(mcpToolProvider)
      .build();

    return assistant;
  }
}
