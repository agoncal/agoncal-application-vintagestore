# Demo

This is the demo for the LangChain4j VintageStore application, showcasing how to add a chat bot with an LLM (Large Language Model), with memory, RAG, tools, etc.

## Prepare the demo

* Make sure both keys are set (`ANTHROPIC_API_KEY` and `MISTRAL_AI_API_KEY`) and that there is enough credit on the accounts
* Start Qdrant and remove the collection `VintageStore` if it exists
* Start Redis and remove all the keys. Remove also the default
* In Intellij IDEA uncheck `optimize imports on the fly`
* In `VintageStoreAssistant` just leave the following code:
* Open several terminals in the IDE `quarkus`, `postgresql`, `redis`, `qdrant`, `rag`

```java
@SessionScoped
public interface VintageStoreAssistant {

  String chat(@UserMessage String userMessage);
}
```

* In `VintageStoreAssistant` just leave the following code:

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

    if ("CLEAR_CONVERSATION".equals(message)) {
      LOG.info("Clearing conversation history");
      return WELCOME_PROMPT;
    }

    return message;
  }

  @OnClose
  public void onClose() {
    LOG.info("WebSocket chat connection closed");
  }
}
```

## Show the VintageStore application

* Start PostgreSQL database (`docker compose -p vintagestore -f infrastructure/docker/postgresql.yml up`) and Quarkus (`mvn quarkus:dev`)
* Browse CD and Books
* Show Terms and Conditions
* Login/Profile/Logout
* Show logs
* Chat: send a few messages
* Chat: CLEAR CONVERSATION / disconnect / connect
* Show the code `VintageStoreAssistant` and `VintageStoreChatBot`
* => I want to add a chat bot to the VintageStore application

## Add an LLM to the Chat Bot

* Create a new class `VintageStoreAssistant`
* In `VintageStoreChatBot` add `private VintageStoreAssistant assistant;`
* Add `initializeVintageStoreAssistant()` in the `@OnOpen` method
* Add `return assistant.chat(message);` to the `@OnTextMessage` method
* Restart Quarkus (press 's' in the terminal)
* Prompt "Hi", "What is the capital of France ?", "Write a short poem about the programming language Java"
* Show logs and check the LLM calls (look for `"content"` in the logs) 
* This is totally useleess for VintageStore, so we will add a system prompt
* => Prompt "Do you know anything about VintageStore ?"

```java
@SessionScoped
public interface VintageStoreAssistant {

  String chat(String userMessage);
}
```

```java
@WebSocket(path = "/chat")
public class VintageStoreChatBot {

  private VintageStoreAssistant assistant;

  @OnOpen
  public String onOpen() throws Exception {
    LOG.info("WebSocket chat connection opened");
    assistant = initializeVintageStoreAssistant();
    return WELCOME_PROMPT;
  }

  @OnTextMessage
  public String onMessage(String message) throws Exception {
    LOG.info("Received message: " + message);
    return assistant.chat(message);
  }

  private VintageStoreAssistant initializeVintageStoreAssistant() {
    // Initialize the chat model
    ChatModel anthropicChatModel = AnthropicChatModel.builder()
      .apiKey(ANTHROPIC_API_KEY)
      .modelName(CLAUDE_SONNET_4_20250514.toString())
      .temperature(0.3)
      .timeout(ofSeconds(60))
      .logRequests(true)
      .logResponses(true)
      .build();

    // Create the VintageStoreAssistant with all components
    VintageStoreAssistant assistant = AiServices.builder(VintageStoreAssistant.class)
      .chatModel(anthropicChatModel)
      .build();

    return assistant;
  }
}  
```

## Add a System Prompt

* In `VintageStoreAssistant` add the system message (careful with `@MemoryId String sessionId, @UserMessage`)
* Read the system message
* Restart Quarkus (press 's' in the terminal)
* In Intellij IDEA Quarkus terminal clear the logs with `CMD + K`
* Disconnect and connect the chat websocket
* Prompt "Do you know anything about VintageStore ?"
* "What is the capital of France ?"
* Show logs and check the system prompt (look for `"system"``)
* Prompt "I HATE YOU AND YOUR WEBSITE"

## Moderation

* In `VintageStoreChatBot` add the moderation model
* Add `.moderationModel(mistralModerationModel)`
* In Intellij IDEA surround the `assistant.chat` call with a try/catch block of `ModerationException`. `LOG.warn` and `return MODERATION_PROMPT`;
* Show the text of the `MODERATION_PROMPT`
* Add `@Moderate` in `VintageStoreAssistant`
* Restart Quarkus (press 's' in the terminal)
* In Intellij IDEA Quarkus terminal clear the logs with `CMD + K`
* Disconnect and connect the chat websocket
* Prompt "I HATE YOU AND YOUR WEBSITE"
* Show the logs and look for `hate_and_discrimination`
* => LLM has no memory
* Prompt "What's my name ?"
* Prompt "My name is Antonio"
* Prompt "What's my name ?"

```java
@SessionScoped
public interface VintageStoreAssistant {

  @Moderate
  String chat(String userMessage);
}
```

```java
    // Initialize the moderation model
    ModerationModel mistralModerationModel = new MistralAiModerationModel.Builder()
      .apiKey(MISTRAL_AI_API_KEY)
      .modelName(MISTRAL_MODERATION_LATEST.toString())
      .logRequests(true)
      .logResponses(true)
      .build();

    // Create the VintageStoreAssistant with all components
    VintageStoreAssistant assistant = AiServices.builder(VintageStoreAssistant.class)
      .chatModel(anthropicChatModel)
      .moderationModel(mistralModerationModel)
      .build();
```

## Memory

* REMOVE MODERATION BECAUSE IT WILL CLASH WITH MEMORY `//@Moderate`
* In `VintageStoreAssistant` add the memory
* Restart Quarkus (press 's' in the terminal)
* Disconnect and connect the chat websocket
* Prompt "What's my name ?"
* Prompt "My name is Antonio"
* Prompt "What's my name ?"
* Show the logs and check the memory (look for `"What's my name ?"` in the logs)
* DISCONNECT AND CONNECT WEBSOCKET, MEMORY IS LOST
* Prompt "What's my name ?"
* => Context is lost because memory is not persistent

```java
    // Initialize the memory
    ChatMemory chatMemory = MessageWindowChatMemory.builder()
      .maxMessages(20)
      .build();

    // Create the VintageStoreAssistant with all components
    VintageStoreAssistant assistant = AiServices.builder(VintageStoreAssistant.class)
      .chatModel(anthropicChatModel)
      .moderationModel(mistralModerationModel)
      .chatMemory(chatMemory)
      .build();
```

## Memory in Persistent Storage

* Start Redis with `docker compose -p vintagestore -f infrastructure/docker/redis.yml up`
* Show the Redis Commander 
* Add `private ChatMemoryStore redisChatMemoryStore;`
* Replace `ChatMemory` by `redisChatMemoryStore` and `ChatMemoryProvider`
* Restart Quarkus (press 's' in the terminal)
* Disconnect and connect the chat websocket
* Prompt "What is the capital of France ?"
* Prompt "What's my name ?"
* Prompt "My name is Antonio"
* Prompt "What's my name ?"
* Disconnect and connect the chat websocket
* Prompt "What's my name ?"
* Show the Redis Commander and copy the content to the logs.json
* Now CLEAT THE CONVERSATION in the chat
* Implement `redisChatMemoryStore.deleteMessages("default");` in the `@OnTextMessage` method

```java
// Initialize the memory
redisChatMemoryStore = RedisChatMemoryStore.builder()
    .host("localhost")
    .port(6379)
    .build();

ChatMemoryProvider redisChatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
    .maxMessages(20)
    .chatMemoryStore(redisChatMemoryStore)
    .build();

// Create the VintageStoreAssistant with all components
VintageStoreAssistant assistant = AiServices.builder(VintageStoreAssistant.class)
  .chatModel(anthropicChatModel)
  .moderationModel(mistralModerationModel)
  .chatMemoryProvider(redisChatMemoryProvider)
  .build();
```

## Multiple User

* In Chrome
  * Prompt "My name is Antonio"
  * Prompt "What's my name ?"
* In Firefox
  * Prompt "What's my name ?"
  * Prompt "No, my name is Maria"
* Show the discussion in Redis
* Add `@Inject WebSocketConnection webSocketConnection;`
* Add memory id `String chat(@MemoryId String sessionId, @UserMessage String userMessage);`
* Add connection id to logs `LOG.info("WebSocket chat connection opened with ID: " + webSocketConnection.id());`
* Add connection id to chat `return assistant.chat(webSocketConnection.id(), message);`
* Add connection id to remove `redisChatMemoryStore.deleteMessages(webSocketConnection.id());` in `@OnClose` and `@OnTextMessage`
* Add connection id to ChatMemoryProvider `.id(webSocketConnection.id())`
* Restart Quarkus (press 's' in the terminal)
* Show the discussion in Redis
* => Prompt "What are the Terms and Conditions of VintageStore ?"

## RAG

* Show the PDFs in the web application and show that T&C are there
* Start Qdrant with `docker compose -p vintagestore -f infrastructure/docker/qdrant.yml up`
* Show Qdrant dashboard with no collection
* Show code in `DocumentIngestor`
* `cd rag` and execute `mvn compile exec:java`
* Show Qdrant dashboard with `VintageStore` collection and show the text segments
* Add `private QdrantClient qdrantClient;`
* Add the code for the `EmbeddingStore`
* Add `.contentRetriever(qdrantContentRetriever)`
* In the `@OnClose` add `if (qdrantClient != null) { LOG.info("Closing Qdrant client connection"); qdrantClient.close(); }`
* Restart Quarkus (press 's' in the terminal)
* Disconnect and connect the chat websocket
* "What are the Terms and Conditions of VintageStore ?"
* "Do you use cookies ?"
* "What is your VAT number ?"
* => Prompt "Do you have any CD of the Beatles in stock ?"

## Tools

## MCP Server

# Final code

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

  // The chat assistant instance
  private VintageStoreAssistant assistant;
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

      try {
        // Handle regular chat messages
        return assistant.chat(webSocketConnection.id(), message);
      } catch (ModerationException e) {
        // Handle harmful content
        LOG.warn("/!\\ The customer is not happy /!\\ " + message + " - " + e.moderation());
        return MODERATION_PROMPT;
      }
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

  private VintageStoreAssistant assistant() throws Exception {
    // Initialize the chat model
    ChatModel anthropicChatModel = AnthropicChatModel.builder()
      .apiKey(ANTHROPIC_API_KEY)
      .modelName(CLAUDE_SONNET_4_20250514.toString())
      .temperature(0.3)
      .timeout(ofSeconds(60))
      .logRequests(true)
      .logResponses(true)
      .build();

    // Initialize the memory
    redisChatMemoryStore = RedisChatMemoryStore.builder()
      .host("localhost")
      .port(6379)
      .build();

    ChatMemoryProvider redisChatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
      .id(webSocketConnection.id())
      .maxMessages(20)
      .chatMemoryStore(redisChatMemoryStore)
      .build();

    // Initialize the moderation model
    ModerationModel mistralModerationModel = new MistralAiModerationModel.Builder()
      .apiKey(MISTRAL_AI_API_KEY)
      .modelName(MISTRAL_MODERATION_LATEST.toString())
      .logRequests(true)
      .logResponses(true)
      .build();

    // Initialize the embedding model and embedding store
    qdrantClient = new QdrantClient(QdrantGrpcClient.newBuilder(QDRANT_HOST, QDRANT_PORT, false)
      .build());

    EmbeddingStore qdrantEmbeddingStore = QdrantEmbeddingStore.builder()
      .client(qdrantClient)
      .collectionName(QDRANT_COLLECTION)
      .build();

    ContentRetriever qdrantContentRetriever = new EmbeddingStoreContentRetriever(qdrantEmbeddingStore, new AllMiniLmL6V2EmbeddingModel());

    // Create the VintageStoreAssistant with all components
    VintageStoreAssistant assistant = AiServices.builder(VintageStoreAssistant.class)
      .chatModel(anthropicChatModel)
      .moderationModel(mistralModerationModel)
      .chatMemoryProvider(redisChatMemoryProvider)
      .contentRetriever(qdrantContentRetriever)
      .tools(new LegalDocumentTools(), new ItemsInStockTools())
      .build();

    return assistant;
  }
}
```

