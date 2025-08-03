package org.agoncal.application.vintagestore.chat;

import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@WebSocket(path = "/chat")
public class VintageStoreChatBot {

  private static final Logger LOG = Logger.getLogger(VintageStoreChatBot.class);
  //  private static final String INDEX_NAME = "VintageStoreIndex";
//  private static final String QDRANT_URL = "http://localhost:6334";
//  private static final String ANTHROPIC_API_KEY = System.getenv("ANTHROPIC_API_KEY");
  private static final String WELCOME_PROMPT = "Hello, how can I help you?";

//  private String SESSION_ID;
  @Inject
  VintageStoreChatAssistant assistant;

  @OnOpen
  public String onOpen() throws Exception {
    LOG.info("WebSocket chat connection opened");
    return WELCOME_PROMPT;
  }

  @OnTextMessage
  public String onMessage(String message) throws Exception {
    LOG.info("Received message: " + message);
    String answer;

    // Handle clear conversation command
    if ("CLEAR_CONVERSATION".equals(message)) {
      LOG.info("Clearing conversation history");
      // Reinitialize assistant to clear memory
      answer = assistant.chat(WELCOME_PROMPT);
    } else {
      answer = assistant.chat(message);
    }

    // Handle regular chat messages
    LOG.debug("Response sent: " + answer);

    return answer;
  }

  @OnClose
  public void onClose() {
    LOG.info("WebSocket chat connection closed");
  }

//  static EmbeddingStore<TextSegment> embeddingStore() throws Exception {
//    String qdrantHostname = new URI(QDRANT_URL).getHost();
//    int qdrantPort = new URI(QDRANT_URL).getPort();
//    QdrantGrpcClient.Builder grpcClientBuilder = QdrantGrpcClient.newBuilder(qdrantHostname, qdrantPort, false);
//    QdrantClient qdrantClient = new QdrantClient(grpcClientBuilder.build());
//
//    EmbeddingStore embeddingStore = QdrantEmbeddingStore.builder()
//      .client(qdrantClient)
//      .collectionName(INDEX_NAME)
//      .build();
//
//    return embeddingStore;
//  }

//  VintageStoreChatAssistant assistant() throws Exception {
//    EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
//    ContentRetriever contentRetriever = new EmbeddingStoreContentRetriever(embeddingStore(), embeddingModel);
//
//    ChatModel model = AnthropicChatModel.builder()
//      .apiKey(ANTHROPIC_API_KEY)
//      .modelName("claude-sonnet-4-20250514")
//      .temperature(0.3)
//      .timeout(ofSeconds(60))
//      .build();
//
////    ChatMemoryStore memoryStore = RedisChatMemoryStore.builder()
////      .host("localhost")
////      .port(6379)
////      .build();
//
//    ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
//      .id(SESSION_ID)
//      .maxMessages(20)
////      .chatMemoryStore(memoryStore)
//      .build();
//
//    VintageStoreChatAssistant assistant = AiServices.builder(VintageStoreChatAssistant.class)
//      .chatModel(model)
//      .chatMemoryProvider(chatMemoryProvider)
//      .contentRetriever(contentRetriever)
//      .tools(new LegalDocumentTools(), new ItemsInStockTools(), new UserLoggedInTools())
//      .build();
//
//    return assistant;
//  }
}
