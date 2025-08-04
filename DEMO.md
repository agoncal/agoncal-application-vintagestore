# Demo

This is the demo for the LangChain4j VintageStore application, showcasing how to add a chat bot with an LLM (Large Language Model), with memory, RAG, tools, etc.

## Prepare the demo

* Make sure both keys are set (`ANTHROPIC_API_KEY` and `MISTRAL_AI_API_KEY`) and that there is enough credit on the accounts
* Start Qdrant and remove the collection `VintageStore` if it exists
* In Intellij IDEA uncheck `optimize imports on the fly`
* In `VintageStoreChatAssistant` just leave the following code:

```java
@SessionScoped
public interface VintageStoreChatAssistant {

  String chat(@UserMessage String userMessage);
}
```

* In `VintageStoreChatAssistant` just leave the following code:

```java
@WebSocket(path = "/chat")
public class VintageStoreChatBot {

  private static final Logger LOG = Logger.getLogger(VintageStoreChatBot.class);

  // Constants for Qdrant configuration
  private static final String QDRANT_COLLECTION = "VintageStore";
  private static final String QDRANT_HOST = "localhost";
  private static final int QDRANT_PORT = 6334;
  // Anthropic API key from environment variable
  private static final String ANTHROPIC_API_KEY = System.getenv("ANTHROPIC_API_KEY");
  private static final String MISTRAL_AI_API_KEY = System.getenv("MISTRAL_AI_API_KEY");
  // Prompts
  private static final String WELCOME_PROMPT = "Hello, how can I help you?";
  private static final String MODERATION_PROMPT = "I don't know why you are frustrated, but I will redirect you to a human assistant who can help you better. Please wait a moment...";

  
  @OnOpen
  public String onOpen() throws Exception {
    LOG.info("WebSocket chat connection opened");
    return WELCOME_PROMPT;
  }

  @OnTextMessage
  public String onMessage(String message) throws Exception {
    LOG.info("Received message: " + message);
    return message;
  }

  @OnClose
  public void onClose() {
    LOG.info("WebSocket chat connection closed");
  }
}
```

## Show the VintageStore application

* Browse CD and Books
* Show Terms and Conditions
* Login/Profile/Logout
* Show logs
* Chat: disconnect/connect/send a message
* Chat: CLEAR CONVERSATION
* Show the code `VintageStoreChatAssistant` and `VintageStoreChatBot`
* => I want to add a chat bot to the VintageStore application

## Add an LLM to the Chat Bot

* In `VintageStoreChatBot` add the `VintageStoreChatAssistant` and the `assistant()` method
* Add `assistant()` in the `@OnOpen` method
* In `VintageStoreChatBot` add `assistant()` and `model()` methods
* Add the Assistant in the `@OnOpen` and `@OnTextMessage` methods
* Restart Quarkus (press 's' in the terminal)
* Prompt "Hi", "What is the capital of France ?", "Write a short poem about the programming language Java"
* Show logs and check the LLM calls
  => Prompt "Do you know anything about VintageStore ?"
* Prompt "Do you know anything about VintageStore ?"

```java
@WebSocket(path = "/chat")
public class VintageStoreChatBot {

  private VintageStoreChatAssistant assistant;

  
  @OnOpen
  public String onOpen() throws Exception {
    LOG.info("WebSocket chat connection opened");
    assistant = assistant();
    return WELCOME_PROMPT;
    assistant = assistant(model());
    String answer = assistant.chat(WELCOME_PROMPT);
    return answer;
  }

  @OnTextMessage
  public String onMessage(String message) throws Exception {
    LOG.info("Received message: " + message);
    String answer = assistant.chat(message);
    String answer;
    answer = assistant.chat(message);
    return answer;
  }

  private VintageStoreChatAssistant assistant() {
    // Initialize the chat model
    ChatModel anthropicClaudeSonnetModel = AnthropicChatModel.builder()
  static VintageStoreChatAssistant assistant(ChatModel model) {
    VintageStoreChatAssistant assistant = AiServices.builder(VintageStoreChatAssistant.class)
      .chatModel(model)
      .build();

    return assistant;
  }

  static ChatModel model() {
    ChatModel model = AnthropicChatModel.builder()
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
    return model;
  }
}

}  
```

## Add a System Prompt
## System Prompt

* In `VintageStoreChatAssistant` add the system message
* Read the system message
* In `VintageStoreChatAssistant` add the system prompt
* Show logs and check the system prompt
* Restart Quarkus (press 's' in the terminal)
* Prompt "Do you know anything about VintageStore ?"
* Show logs and check the system prompt
* => LLM has no memory
* Prompt "What's my name ?"
* Prompt "My name is Antonio"
* Prompt "What's my name ?"

## Memory

* In `VintageStoreChatAssistant` add the memory
* Restart Quarkus (press 's' in the terminal)
* Disconnect and connect the chat because the Assistant is initialized at the `@OnOpen`
* Prompt "What's my name ?"
* Prompt "My name is Antonio"
* Prompt "What's my name ?"
* DISCONNECT AND CONNECT WEBSOCKET, MEMORY IS LOST, NOT IN PERSISTENT STORAGE
* Prompt "My name is Antonio"

```java
  @OnOpen
  public String onOpen() throws Exception {
    assistant = assistant(model(), memory());
    // ...
  }


  static ChatMemory memory() {
    ChatMemory chatMemory = MessageWindowChatMemory.builder()
      .maxMessages(20)
      .build();

    return chatMemory;
  }

  static VintageStoreChatAssistant assistant(ChatModel model, ChatMemory memory) {
    VintageStoreChatAssistant assistant = AiServices.builder(VintageStoreChatAssistant.class)
      .chatModel(model)
      .chatMemory(memory)
      .build();
  
    return assistant;
  }
```

## Memory in Persistent Storage

* Start Redis with `docker compose -f src/main/docker/redis.yml up -d`
* Prompt "What's my name ?"
* Prompt "My name is Antonio"
* Prompt "What's my name ?"
* DISCONNECT AND CONNECT WEBSOCKET, MEMORY IS LOST, NOT IN PERSISTENT STORAGE
* Prompt "My name is Antonio"
* Show the Redis Commander and copy the content to the logs.json

```java
  static ChatMemory memory() {
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
```


```java
@WebSocket(path = "/chat")
public class VintageStoreChatBot {

  private static final Logger LOG = Logger.getLogger(VintageStoreChatBot.class);
  // Constants for Qdrant configuration
  private static final String QDRANT_COLLECTION = "VintageStore";
  private static final String QDRANT_HOST = "localhost";
  private static final int QDRANT_PORT = 6334;
  // Anthropic API key from environment variable
  private static final String ANTHROPIC_API_KEY = System.getenv("ANTHROPIC_API_KEY");
  private static final String WELCOME_PROMPT = "Hello, how can I help you?";

  // The chat assistant instance
  private VintageStoreChatAssistant assistant;
  private QdrantClient qdrantClient;
  private ChatMemoryStore redisChatMemoryStore;

  @Inject
  WebSocketConnection webSocketConnection;

  @OnOpen
  public String onOpen() throws Exception {
    LOG.info("WebSocket chat connection opened with ID: " + webSocketConnection.id());
    assistant = assistant();
    return WELCOME_PROMPT;
  }

  @OnTextMessage
  public String onMessage(String message) throws Exception {
    LOG.info("Received message: " + message + " from connection ID: " + webSocketConnection.id());

    if ("CLEAR_CONVERSATION".equals(message)) {
      // Handle clear conversation command
      LOG.info("Clearing conversation history");
      redisChatMemoryStore.deleteMessages(webSocketConnection.id());
      return WELCOME_PROMPT;
    } else {
      // Handle regular chat messages
      String answer = assistant.chat(webSocketConnection.id(), message);
      LOG.debug("Response sent: " + answer);
      return answer;
    }
  }

  @OnClose
  public void onClose() {
    LOG.info("WebSocket chat connection closed with ID: " + webSocketConnection.id());
    redisChatMemoryStore.deleteMessages(webSocketConnection.id());
    if (qdrantClient != null) {
      LOG.info("Closing Qdrant client connection");
      qdrantClient.close();
    }
  }

  private VintageStoreChatAssistant assistant() throws Exception {
    // Initialize the chat model
    ChatModel anthropicClaudeSonnetModel = AnthropicChatModel.builder()
      .apiKey(ANTHROPIC_API_KEY)
      .modelName("claude-sonnet-4-20250514")
      .temperature(0.3)
      .timeout(ofSeconds(60))
      .logRequests(true)
      .logResponses(true)
      .build();

    // Initialize the chat memory store and provider
    redisChatMemoryStore = RedisChatMemoryStore.builder()
      .host("localhost")
      .port(6379)
      .build();

    ChatMemoryProvider redisChatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
      .id(webSocketConnection.id())
      .maxMessages(20)
      .chatMemoryStore(redisChatMemoryStore)
      .build();

    // Initialize the embedding model and Qdrant client
    qdrantClient = new QdrantClient(QdrantGrpcClient.newBuilder(QDRANT_HOST, QDRANT_PORT, false)
      .build());

    EmbeddingStore qdrantEmbeddingStore = QdrantEmbeddingStore.builder()
      .client(qdrantClient)
      .collectionName(QDRANT_COLLECTION)
      .build();

    ContentRetriever qdrantContentRetriever = new EmbeddingStoreContentRetriever(qdrantEmbeddingStore, new AllMiniLmL6V2EmbeddingModel());

    // Create the VintageStoreChatAssistant with all components
    VintageStoreChatAssistant assistant = AiServices.builder(VintageStoreChatAssistant.class)
      .chatModel(anthropicClaudeSonnetModel)
      .chatMemoryProvider(redisChatMemoryProvider)
      .contentRetriever(qdrantContentRetriever)
      .tools(new LegalDocumentTools(), new ItemsInStockTools())
      .build();

    return assistant;
  }
}
```

