package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
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

import static java.time.Duration.ofSeconds;

@WebSocket(path = "/chat")
public class VintageStoreChatBot {

  private static final Logger LOG = Logger.getLogger(VintageStoreChatBot.class);
  // Constants for Qdrant configuration
  private static final String QDRANT_COLLECTION = "VintageStoreIndex";
  private static final String QDRANT_HOST = "localhost";
  private static final int QDRANT_PORT = 6334;
  // Anthropic API key from environment variable
  private static final String ANTHROPIC_API_KEY = System.getenv("ANTHROPIC_API_KEY");
  private static final String WELCOME_PROMPT = "Hello, how can I help you?";

  private VintageStoreChatAssistant assistant;

  @OnOpen
  public String onOpen() throws Exception {
    LOG.info("WebSocket chat connection opened");
    assistant = assistant();
    return WELCOME_PROMPT;
  }

  @OnTextMessage
  public String onMessage(String message) throws Exception {
    LOG.info("Received message: " + message);
    String answer = assistant.chat(message);
    return answer;
  }

  @OnClose
  public void onClose() {
    LOG.info("WebSocket chat connection closed");
  }

  private VintageStoreChatAssistant assistant() {
    // Initialize the chat model
    ChatModel anthropicClaudeSonnetModel = AnthropicChatModel.builder()
      .apiKey(ANTHROPIC_API_KEY)
      .modelName("claude-sonnet-4-20250514")
      .temperature(0.3)
      .timeout(ofSeconds(60))
      .logRequests(true)
      .logResponses(true)
      .build();

    // Create the VintageStoreChatAssistant
    VintageStoreChatAssistant assistant = AiServices.builder(VintageStoreChatAssistant.class)
      .chatModel(anthropicClaudeSonnetModel)
      .build();

    return assistant;
  }
}
