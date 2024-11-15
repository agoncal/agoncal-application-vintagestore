package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.store.memory.chat.redis.RedisChatMemoryStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import static java.time.Duration.ofSeconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@WebSocket(path = "/chat")
public class VintageStoreChatBot {

  private static final Logger LOG = LoggerFactory.getLogger(VintageStoreChatBot.class);

  private static final String INDEX_NAME = "VintageStoreIndex";
  private static final String QDRANT_URL = "http://localhost:6334";
  private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

  private VintageStoreChatAssistant assistant;

  @OnOpen
  public String onOpen() throws Exception {
    EmbeddingStore<TextSegment> embeddingStore = embeddingStore();
    ChatLanguageModel model = model();
    assistant = assistant(embeddingStore, model);

    String answer = assistant.chat("Hello, how can I help you?");
    LOG.info(answer);

    return answer;
  }

  @OnTextMessage
  public String onMessage(String message) {
    String answer = assistant.chat(message);
    LOG.info(answer);

    return answer;
  }

  static EmbeddingStore<TextSegment> embeddingStore() throws Exception {
    String qdrantHostname = new URI(QDRANT_URL).getHost();
    int qdrantPort = new URI(QDRANT_URL).getPort();
    QdrantGrpcClient.Builder grpcClientBuilder = QdrantGrpcClient.newBuilder(qdrantHostname, qdrantPort, false);
    QdrantClient qdrantClient = new QdrantClient(grpcClientBuilder.build());

    EmbeddingStore embeddingStore = QdrantEmbeddingStore.builder()
      .client(qdrantClient)
      .collectionName(INDEX_NAME)
      .build();

    return embeddingStore;
  }

  static ChatLanguageModel model() {
    ChatLanguageModel model = OpenAiChatModel.builder()
      .apiKey(OPENAI_API_KEY)
      .modelName(GPT_4_O)
      .temperature(0.3)
      .timeout(ofSeconds(60))
      .logRequests(true)
      .logResponses(true)
      .build();

    return model;
  }

  static VintageStoreChatAssistant assistant(EmbeddingStore<TextSegment> embeddingStore,
                                                     ChatLanguageModel model) {
    EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    ContentRetriever contentRetriever = new EmbeddingStoreContentRetriever(embeddingStore, embeddingModel);

    VintageStoreChatAssistant assistant = AiServices.builder(VintageStoreChatAssistant.class)
      .chatLanguageModel(model)
      .chatMemory(chatMemory())
      .contentRetriever(contentRetriever)
      .tools(new LegalDocumentTools(), new ItemsInStockTools())
      .build();

    return assistant;
  }

  private static ChatMemory chatMemory() {
    ChatMemoryStore memoryStore = RedisChatMemoryStore.builder()
      .host("localhost")
      .port(6379)
      .build();

    ChatMemory chatMemory = MessageWindowChatMemory.builder()
      .maxMessages(20)
      .chatMemoryStore(memoryStore)
      .build();

    return chatMemory;
  }
}
