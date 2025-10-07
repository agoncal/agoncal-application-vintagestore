package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_1_MINI;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiModerationModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.ModerationException;
import dev.langchain4j.service.Result;
import dev.langchain4j.store.embedding.azure.search.AzureAiSearchEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.inject.Inject;
import org.agoncal.application.vintagestore.summarize.OpenAISummarizer;
import org.agoncal.application.vintagestore.summarize.SummarizingTokenWindowChatMemory;
import org.agoncal.application.vintagestore.tool.ItemsInStockTools;
import org.agoncal.application.vintagestore.tool.LegalDocumentTools;
import org.agoncal.application.vintagestore.tool.UserLoggedInTools;
import org.jboss.logging.Logger;

import static java.time.Duration.ofSeconds;

@WebSocket(path = "/chat")
public class VintageStoreAzureBot {

  private static final Logger LOG = Logger.getLogger(VintageStoreAzureBot.class);

  // AI-Model API keys from environment variable
  private static final String AZURE_AI_FOUNDRY_KEY = System.getenv("AZURE_AI_FOUNDRY_KEY");
  private static final String AZURE_AI_FOUNDRY_ENDPOINT = System.getenv("AZURE_AI_FOUNDRY_ENDPOINT");
  private static final String CHAT_MODEL = "vintagestore-chat-model";
  private static final String MODERATION_MODEL = "vintagestore-moderation-model";
  private static final String EMBEDDING_MODEL = "vintagestore-embedding-model";
  private static final String SUMMARIZATION_MODEL = "vintagestore-summarization-model";
  // Constants for Azure AI Search configuration
  private static final String AZURE_SEARCH_ENDPOINT = System.getenv("AZURE_SEARCH_ENDPOINT");
  private static final String AZURE_SEARCH_KEY = System.getenv("AZURE_SEARCH_KEY");
  private static final String AZURE_SEARCH_COLLECTION = "VintageStore";
  // Constants for Redis configuration
  private static final String REDIS_HOSTNAME = System.getenv("REDIS_HOSTNAME");
  private static final int REDIS_PORT = 6379;
  // Prompts
  private static final String WELCOME_PROMPT = "Hello, how can I help you?";
  private static final String MODERATION_PROMPT = "I don't know why you are frustrated, but I will redirect you to a human assistant who can help you better. Please wait a moment...";
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
    } catch (ModerationException e) {
      LOG.warn("/!\\ The customer is not happy /!\\ " + message + " - " + e.moderation());
      return MODERATION_PROMPT;
    }
  }

  @OnClose
  public void onClose() {
    LOG.info("WebSocket chat connection closed with session id " + sessionId());
    redisChatMemoryStore.deleteMessages(webSocketConnection.id());
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

    // Initialize the chat model
    ChatModel chatModel = OpenAiChatModel.builder()
      .apiKey(AZURE_AI_FOUNDRY_KEY)
      .baseUrl(AZURE_AI_FOUNDRY_ENDPOINT)
      .modelName(CHAT_MODEL)
      .temperature(0.3)
      .timeout(ofSeconds(60))
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();

    // Initialize the moderation model
    ModerationModel moderationModel = OpenAiModerationModel.builder()
      .apiKey(AZURE_AI_FOUNDRY_KEY)
      .baseUrl(AZURE_AI_FOUNDRY_ENDPOINT)
      .modelName(MODERATION_MODEL)
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();

    // Initialize the embedding model
    EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
      .apiKey(AZURE_AI_FOUNDRY_KEY)
      .baseUrl(AZURE_AI_FOUNDRY_ENDPOINT)
      .modelName(EMBEDDING_MODEL)
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();

    // Initialize the summary model
    ChatModel summarizationModel = OpenAiChatModel.builder()
      .apiKey(AZURE_AI_FOUNDRY_KEY)
      .baseUrl(AZURE_AI_FOUNDRY_ENDPOINT)
      .modelName(SUMMARIZATION_MODEL)
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();

    // Initialize the memory
    redisChatMemoryStore = RedisChatMemoryStore.builder()
      .host(REDIS_HOSTNAME)
      .port(REDIS_PORT)
      .build();

    ChatMemoryProvider redisChatMemoryProvider = memoryId -> SummarizingTokenWindowChatMemory.builder()
      .id(webSocketConnection.id())
      .maxTokens(MAX_TOKENS, new OpenAiTokenCountEstimator(GPT_4_1_MINI))
      .summarizer(new OpenAISummarizer((OpenAiChatModel) summarizationModel, TOKEN_LIMIT))
      .chatMemoryStore(redisChatMemoryStore)
      .build();

    // Initialize the embedding model and embedding store
    AzureAiSearchEmbeddingStore azureAiSearchEmbeddingStore = AzureAiSearchEmbeddingStore.builder()
      .endpoint(AZURE_SEARCH_ENDPOINT)
      .apiKey(AZURE_SEARCH_KEY)
      .indexName(AZURE_SEARCH_COLLECTION)
      .dimensions(embeddingModel.dimension())
      .build();

    ContentRetriever contentRetriever = new EmbeddingStoreContentRetriever(azureAiSearchEmbeddingStore, embeddingModel);

    // MCP Currency
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
      .build();

    // Create the VintageStoreAssistant with all components
    VintageStoreAssistant assistant = AiServices.builder(VintageStoreAssistant.class)
      .chatModel(chatModel)
      .moderationModel(moderationModel)
      .chatMemoryProvider(redisChatMemoryProvider)
      .contentRetriever(contentRetriever)
      .tools(new LegalDocumentTools(), new ItemsInStockTools(), new UserLoggedInTools())
      .toolProvider(mcpToolProvider)
      .build();

    return assistant;
  }
}
