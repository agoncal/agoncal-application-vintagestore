package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
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
import org.jboss.logging.Logger;

import java.net.URI;
import static java.time.Duration.ofSeconds;

@WebSocket(path = "/chat")
public class VintageStoreChatBot {

  private static final Logger LOG = Logger.getLogger(VintageStoreChatBot.class);
  private static final String INDEX_NAME = "VintageStoreIndex";
  private static final String QDRANT_URL = "http://localhost:6334";
  private static final String ANTHROPIC_API_KEY = System.getenv("ANTHROPIC_API_KEY");
  private static final String WELCOME_PROMPT = "Hello, how can I help you?";

  private VintageStoreChatAssistant assistant;
  private QdrantClient qdrantClient;

  @Inject
  WebSocketConnection connection;

  @OnOpen
  public String onOpen() throws Exception {
    LOG.info("WebSocket chat connection opened with ID: " + connection.id());
    assistant = assistant();
    return WELCOME_PROMPT;
  }

  @OnTextMessage
  public String onMessage(String message) throws Exception {
    LOG.info("Received message: " + message + " from connection ID: " + connection.id());

    if ("CLEAR_CONVERSATION".equals(message)) {
      // Handle clear conversation command
      LOG.info("Clearing conversation history");
      return WELCOME_PROMPT;
    } else {
      // Handle regular chat messages
      String answer = assistant.chat(connection.id(), message);
      LOG.debug("Response sent: " + answer);
      return answer;
    }
  }

  @OnClose
  public void onClose() {
    LOG.info("WebSocket chat connection closed with ID: " + connection.id());
    qdrantClient.close();
  }

  EmbeddingStore<TextSegment> embeddingStore() throws Exception {
    String qdrantHostname = new URI(QDRANT_URL).getHost();
    int qdrantPort = new URI(QDRANT_URL).getPort();
    QdrantGrpcClient.Builder grpcClientBuilder = QdrantGrpcClient.newBuilder(qdrantHostname, qdrantPort, false);
    qdrantClient = new QdrantClient(grpcClientBuilder.build());

    EmbeddingStore embeddingStore = QdrantEmbeddingStore.builder()
      .client(qdrantClient)
      .collectionName(INDEX_NAME)
      .build();

    return embeddingStore;
  }

  VintageStoreChatAssistant assistant() throws Exception {
    EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    ContentRetriever contentRetriever = new EmbeddingStoreContentRetriever(embeddingStore(), embeddingModel);

    ChatModel model = AnthropicChatModel.builder()
      .apiKey(ANTHROPIC_API_KEY)
      .modelName("claude-sonnet-4-20250514")
      .temperature(0.3)
      .timeout(ofSeconds(60))
      .logRequests(true)
      .logResponses(true)
      .build();

    ChatMemoryStore memoryStore = RedisChatMemoryStore.builder()
      .host("localhost")
      .port(6379)
      .build();

    ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
      .id(connection.id())
      .maxMessages(20)
      .chatMemoryStore(memoryStore)
      .build();

    VintageStoreChatAssistant assistant = AiServices.builder(VintageStoreChatAssistant.class)
      .chatModel(model)
      .chatMemoryProvider(chatMemoryProvider)
      .contentRetriever(contentRetriever)
      .tools(new LegalDocumentTools(), new ItemsInStockTools())
      .build();

    return assistant;
  }
}
