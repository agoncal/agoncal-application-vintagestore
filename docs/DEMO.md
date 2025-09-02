# Demo

This is the demo for the LangChain4j VintageStore application, showcasing how to add a chat bot with an LLM (Large Language Model), with memory, RAG, tools, etc.

## 00 - Prepare the demo

* Make sure both keys are set and that there is enough credit on the accounts
  * `ANTHROPIC_API_KEY`: https://console.anthropic.com/settings/keys
  * `MISTRAL_AI_API_KEY`: https://admin.mistral.ai/organization/api-keys
  * `COHERE_API_KEY`: https://dashboard.cohere.com/api-keys
  * `OPENAI_API_KEY`: https://platform.openai.com/api-keys
* Make sure all Docker Compose are working
  * PostgreSQL: `docker compose -p vintagestore -f infrastructure/docker/postgresql.yml up -d`
  * Qdrant: `docker compose -p vintagestore -f infrastructure/docker/qdrant.yml up -d`
  * Redis: `docker compose -p vintagestore -f infrastructure/docker/redis.yml up -d`
  * MCP Currency: `docker compose -p vintagestore -f infrastructure/docker/mcp-currency.yml up -d`
* Start Qdrant and remove the collection `VintageStore` if it exists http://localhost:6333/dashboard
* Start Redis and remove all the keys. Remove also the default http://localhost:8089
* In Intellij IDEA uncheck `optimize imports on the fly`
* Open several terminals in the IDE `quarkus`, `rag`
* Stop all the running Docker containers
* Open 2 Browsers: one for Chrome and one for Firefox
* In `VintageStoreAssistant` just leave the following code:

```java
@SessionScoped
public interface VintageStoreAssistant {

  String chat(String userMessage);
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

## 01 - Show the VintageStore application

* Start PostgreSQL database (`docker compose -p vintagestore -f infrastructure/docker/postgresql.yml up`) and Quarkus (`mvn quarkus:dev`)
* Browse CD and Books
* Show Terms and Conditions
* Login/Profile/Logout
* Show logs
* Chat: send a few messages
* Chat: CLEAR CONVERSATION / disconnect / connect
* Show the code `VintageStoreAssistant` and `VintageStoreChatBot`
* => I want to add a chat bot to the VintageStore application

## 10 - Add an LLM to the Chat Bot (lc-llm)

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

## 11 - Add a System Prompt  (lc-prompt)

* In `VintageStoreAssistant` add the system message (careful with `@MemoryId String sessionId, @UserMessage`)
* Read the system message
* Restart Quarkus (press 's' in the terminal)
* In Intellij IDEA Quarkus terminal clear the logs with `CMD + K`
* Disconnect and connect the chat websocket
* Prompt "Do you know anything about VintageStore ?"
* "What is the capital of France ?"
* Show logs and check the system prompt (look for `"system"`) and look for `The current date is`
* Prompt "I HATE YOU AND YOUR WEBSITE"

## 12 - Moderation (lc-moderate)

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

## 20 - Memory (lc-memory)

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

## 21 - Memory in Persistent Storage (lc-redis)

* Start Redis with `docker compose -p vintagestore -f infrastructure/docker/redis.yml up`
* Show the Redis Commander http://localhost:8089
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

## 22 - Multiple User Chat History

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

## 30 - RAG (lc-rag)

* Show the PDFs in the web application and show that T&C are there
* Start Qdrant with `docker compose -p vintagestore -f infrastructure/docker/qdrant.yml up`
* Show Qdrant dashboard with no collection (http://localhost:6333/dashboard)
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
* "What is your VAT number ?"
* "What are the currencies I can pay with ?"
* => Prompt "Give me all my user details"
* => Prompt "Do you have any book on Java ?"
* => Prompt "What are the top-rated CDs ?"

## 40 - Tools (lc-tools)

* "Give me all my user details"
* "Do you have any book on Java ?"
* "What are the top-rated CDs ?"
* => Prompt "What are the currencies I can pay with ?"
* => Prompt "You use dollars. But how much is the book in Euros ?"

## 41 - MCP (lc-mcp)

* Show the code of the MCPServerCurrency
* Add the MCP client in `VintageStoreChatBot` and then add it to the bot
* In Intellij IDEA Quarkus terminal clear the logs with `CMD + K`
* "How much is the book in Euros ?"
* Show the log `dollars to euros:`

```java
.toolProvider(toolProvider)
```

Docker commands in case:

```shell
docker run --interactive --tty --name mcp-currency vintagestore/mcp-currency:latest
docker compose -p vintagestore -f infrastructure/docker/mcp-currency.yml up
```

## 50 - Token consumption (lc-token)

* Replace method signature `Result<String> chat(@MemoryId String sessionId, @UserMessage String userMessage);`
* Change logging level to WARN in `application.properties`:
```properties
quarkus.log.category."dev.langchain4j".level=WARN
quarkus.log.category."org.agoncal.application.vintagestore".level=WARN
```
* Replace `assistant.chat` with `lc-token`. Log tokens in red in WARN level:
* Sign-in as `john.doe`
* CLEAN REDIS CONVERSATIONS
* COMMENT `redisChatMemoryStore.deleteMessages` in `OnClose` so we can see the conversation before the error
* "Hi"
* "My favorite band is 'The Beatles'"
* "What are my profile details?"
* "Any books on Java?"
* "What are the top rated CDs ?"
* "What is my favorite band?"
* Show the `rate_limit_error`
* Copy/paste the Redis conversation in the `logs.json` file
* Show the JSon:
  * The system prompt at the top `You are the official customer service`
  * `Answer using the following information` comes from LangChain4j itself `DefaultContentInjector`
  * The RAG in `Hi` with `We encourage you to review this privacy notice`
  * Show the tool invocation `get_current_user_info` after `What are my profile details?`
  * Show tool `get_top_rated_items`
  * Show tool `search_catalog` after `Any books on Java?`
  * And the end before the limit is reached `Give me the prices in Euros`
* DONT USE CHAT TO SEARCH CATALOG

## 51 - Summarizing conversation ()

* In `VintageStoreChatBot` add the summarizer `SummarizingTokenWindowChatMemory`
* "Hi"
* "My favorite band is 'The Beatles'"
* "What are my profile details?"
* "Any books on Java?"
* "What are the top rated CDs?"
* "What is my favorite band?"

## Monitoring

## Guardrails

* User inputs is too long

# Final code

```java
  @SystemMessage("""
    You are the official customer service chatbot for **Vintage Store**. Your primary role is to assist customers with inquiries related to our products, services, policies, and shopping experience.

    ## What is Vintage Store?

    Vintage Store is a specialized e-commerce platform dedicated to vintage and collectible items, particularly focusing on:

    **Product Categories:**
    - **Books**: A curated collection of vintage and rare books across various categories, publishers, and authors
    - **CDs**: Vintage music albums from different genres, labels, and musicians

    **Key Features:**
    - **AI-Powered Shopping Experience**: Advanced chat assistance for personalized product recommendations and customer support
    - **Comprehensive Catalog**: Detailed product information including metadata like publication dates, ISBN numbers, artist details, and more
    - **User Authentication**: Secure sign-in system with user profiles and role-based access
    - **Expert Curation**: Each item is carefully selected for its vintage appeal and collectible value

    **Our Mission**: To connect vintage enthusiasts with authentic, high-quality collectible books and music albums while providing an exceptional digital shopping experience enhanced by AI technology.

    ## Communication Style
    - **Always respond in Markdown format**
    - Keep responses **short, concise and directly relevant** to the customer's question
    - Maintain a **polite, friendly, and professional tone**
    - Use clear, customer-friendly language (avoid jargon)
    - Address customers respectfully but be concise

    ## Knowledge Scope
    You have comprehensive knowledge of:
    - Current inventory and product details
    - Company policies, terms and conditions, and legal information
    - Shipping, returns, and exchange procedures
    - Store hours, locations, and contact information
    - Pricing and promotional offers

    ## Response Protocol

    **For Vintage Store-related questions:**
    - Provide accurate, helpful information directly
    - If you don't know a specific answer, respond with: *"I don't have that information available right now. Please contact our customer service team at [contact@vintagestore.com] or check our website for the most up-to-date details."*
    - Offer relevant alternatives or next steps when possible

    **For non-Vintage Store questions:**
    - Briefly acknowledge the question and provide a helpful response if appropriate
    - Include this disclaimer: *"Please note: I'm Vintage Store's customer service bot and specialize in questions about our products and services. For detailed information outside of Vintage Store topics, I recommend consulting other specialized resources."*

    ## Additional Instructions
    - Always prioritize customer satisfaction and helpfulness
    - When discussing policies, be clear about terms while remaining customer-friendly
    - If a customer seems frustrated, acknowledge their concern and offer solutions
    - For complex issues, guide customers to appropriate human support channels
    """)
  @Moderate
  String chat(@MemoryId String sessionId, @UserMessage String userMessage);
```

```java
package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import static dev.langchain4j.model.anthropic.AnthropicChatModelName.CLAUDE_SONNET_4_20250514;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import static dev.langchain4j.model.mistralai.MistralAiChatModelName.MISTRAL_MODERATION_LATEST;
import dev.langchain4j.model.mistralai.MistralAiModerationModel;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.ModerationException;
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
import org.agoncal.application.vintagestore.tool.ItemsInStockTools;
import org.agoncal.application.vintagestore.tool.LegalDocumentTools;
import org.agoncal.application.vintagestore.tool.UserLoggedInTools;
import org.jboss.logging.Logger;

import static java.time.Duration.ofSeconds;
import java.util.List;

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

  private VintageStoreAssistant assistant;
  private ChatMemoryStore redisChatMemoryStore;
  private QdrantClient qdrantClient;

  @Inject
  WebSocketConnection webSocketConnection;

  @OnOpen
  public String onOpen() throws Exception {
    LOG.info("WebSocket chat connection opened with ID: " + webSocketConnection.id());
    assistant = initializeVintageStoreAssistant();
    return WELCOME_PROMPT;
  }

  @OnTextMessage
  public String onMessage(String message) throws Exception {
    LOG.info("Received message: " + message + " from connection ID: " + webSocketConnection.id());

    if ("CLEAR_CONVERSATION".equals(message)) {
      LOG.info("Clearing conversation history");
      redisChatMemoryStore.deleteMessages(webSocketConnection.id());
      return WELCOME_PROMPT;
    }

    try {
      return assistant.chat(webSocketConnection.id(), message);
    } catch (ModerationException e) {
      LOG.warn("/!\\ The customer is not happy /!\\ " + message + " - " + e.moderation());
      return MODERATION_PROMPT;
    }
  }

  @OnClose
  public void onClose() {
    LOG.info("WebSocket chat connection closed for ID: " + webSocketConnection.id());
    redisChatMemoryStore.deleteMessages(webSocketConnection.id());
    if (qdrantClient != null) {
      LOG.info("Closing Qdrant client connection");
      qdrantClient.close();
    }
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

    // Initialize the moderation model
    ModerationModel mistralModerationModel = new MistralAiModerationModel.Builder()
      .apiKey(MISTRAL_AI_API_KEY)
      .modelName(MISTRAL_MODERATION_LATEST.toString())
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

    // Initialize the embedding model and embedding store
    qdrantClient = new QdrantClient(QdrantGrpcClient.newBuilder(QDRANT_HOST, QDRANT_PORT, false)
      .build());

    EmbeddingStore qdrantEmbeddingStore = QdrantEmbeddingStore.builder()
      .client(qdrantClient)
      .collectionName(QDRANT_COLLECTION)
      .build();

    ContentRetriever qdrantContentRetriever = new EmbeddingStoreContentRetriever(qdrantEmbeddingStore, new AllMiniLmL6V2EmbeddingModel());

    // MCP Currency
    McpTransport transport = new StdioMcpTransport.Builder()
      .command(List.of("/usr/bin/java", "-jar", "/Users/agoncal/Documents/Code/AGoncal/agoncal-application-vintagestore/mcp-currency/target/mcp-currency-1.0.0-SNAPSHOT-runner.jar"))
      .logEvents(true)
      .build();

    McpClient mcpClient = new DefaultMcpClient.Builder()
      .key("VintageStoreMCPClient")
      .transport(transport)
      .build();

    McpToolProvider toolProvider = McpToolProvider.builder()
      .mcpClients(mcpClient)
      .build();

    // Create the VintageStoreAssistant with all components
    VintageStoreAssistant assistant = AiServices.builder(VintageStoreAssistant.class)
      .chatModel(anthropicChatModel)
      .moderationModel(mistralModerationModel)
      .chatMemoryProvider(redisChatMemoryProvider)
      .contentRetriever(qdrantContentRetriever)
      .tools(new LegalDocumentTools(), new ItemsInStockTools(), new UserLoggedInTools())
      .toolProvider(toolProvider)
      .build();

    return assistant;
  }
}
```

## Intellij IDEA Templates

lc-llm - LangChain4j Demo - Add an LLM to the Chat Bot

```java
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
```


lc-memory - LangChain4j Demo - Adds volatile memory

```java
// Initialize the memory
ChatMemory chatMemory = MessageWindowChatMemory.builder()
.maxMessages(20)
.build();
```

lc-moderate - LangChain4j Demo - Adding Moderation

```java
    // Initialize the moderation model
    ModerationModel mistralModerationModel = new MistralAiModerationModel.Builder()
      .apiKey(MISTRAL_AI_API_KEY)
      .modelName(MISTRAL_MODERATION_LATEST.toString())
      .logRequests(true)
      .logResponses(true)
      .build();

    try {
      return assistant.chat(message);
    } catch (ModerationException e) {
      LOG.warn("/!\\ The customer is not happy /!\\ " + message + " - " + e.moderation());
      return MODERATION_PROMPT;
    }

```


lc-prompt - LangChain4j Demo - Add a System Prompt

```java
  @SystemMessage("""
    You are the official customer service chatbot for **Vintage Store**. Your primary role is to assist customers with inquiries related to our products, services, policies, and shopping experience.

    ## What is Vintage Store?

    Vintage Store is a specialized e-commerce platform dedicated to vintage and collectible items, particularly focusing on:

    **Product Categories:**
    - **Books**: A curated collection of vintage and rare books across various categories, publishers, and authors
    - **CDs**: Vintage music albums from different genres, labels, and musicians

    **Key Features:**
    - **AI-Powered Shopping Experience**: Advanced chat assistance for personalized product recommendations and customer support
    - **Comprehensive Catalog**: Detailed product information including metadata like publication dates, ISBN numbers, artist details, and more
    - **User Authentication**: Secure sign-in system with user profiles and role-based access
    - **Expert Curation**: Each item is carefully selected for its vintage appeal and collectible value

    **Our Mission**: To connect vintage enthusiasts with authentic, high-quality collectible books and music albums while providing an exceptional digital shopping experience enhanced by AI technology.

    ## Communication Style
    - **Always respond in Markdown format**
    - Keep responses **short, concise and directly relevant** to the customer's question
    - Maintain a **polite, friendly, and professional tone**
    - Use clear, customer-friendly language (avoid jargon)
    - Address customers respectfully but be concise

    ## Knowledge Scope
    You have comprehensive knowledge of:
    - Current inventory and product details
    - Company policies, terms and conditions, and legal information
    - Shipping, returns, and exchange procedures
    - Store hours, locations, and contact information
    - Pricing and promotional offers

    ## Response Protocol

    **For Vintage Store-related questions:**
    - Provide accurate, helpful information directly
    - If you don't know a specific answer, respond with: *"I don't have that information available right now. Please contact our customer service team at [contact@vintagestore.com] or check our website for the most up-to-date details."*
    - Offer relevant alternatives or next steps when possible

    **For non-Vintage Store questions:**
    - Briefly acknowledge the question and provide a helpful response if appropriate
    - Include this disclaimer: *"Please note: I'm Vintage Store's customer service bot and specialize in questions about our products and services. For detailed information outside of Vintage Store topics, I recommend consulting other specialized resources."*

    ## Additional Instructions
    - Always prioritize customer satisfaction and helpfulness
    - When discussing policies, be clear about terms while remaining customer-friendly
    - If a customer seems frustrated, acknowledge their concern and offer solutions
    - For complex issues, guide customers to appropriate human support channels
    """)
```


lc-rag - LangChain4j Demo - Adding RAG

```java
    // Initialize the embedding model and embedding store
    qdrantClient = new QdrantClient(QdrantGrpcClient.newBuilder(QDRANT_HOST, QDRANT_PORT, false)
      .build());

    EmbeddingStore qdrantEmbeddingStore = QdrantEmbeddingStore.builder()
      .client(qdrantClient)
      .collectionName(QDRANT_COLLECTION)
      .build();

    ContentRetriever qdrantContentRetriever = new EmbeddingStoreContentRetriever(qdrantEmbeddingStore, new AllMiniLmL6V2EmbeddingModel());
```

lc-redis - langChain4j Demo - Adds Redis

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
```

lc-tools - LangChain4j Demo - Adds tools

```java
.tools(new LegalDocumentTools(), new ItemsInStockTools(), new UserLoggedInTools())
```

lc-token - LangChain4j Demo - Logging Tokens

```java
LOG.warn("\u001B[31mTokens: Input (" + response.tokenUsage().inputTokenCount()+"), Output (" + response.tokenUsage().outputTokenCount() + "), Total (" + response.tokenUsage().totalTokenCount
```

lc-mcp - LangChain4j Demo - Adds MCP Client

```java
    // MCP Currency
    McpTransport transport = new StdioMcpTransport.Builder()
      .command(List.of("/usr/bin/java", "-jar", "/Users/agoncal/Documents/Code/AGoncal/agoncal-application-vintagestore/mcp-currency/target/mcp-currency-1.0.0-SNAPSHOT-runner.jar"))
      .logEvents(true) // only if you want to see the traffic in the log
      .build();

    McpClient mcpClient = new DefaultMcpClient.Builder()
      .key("VintageStoreMCPClient")
      .transport(transport)
      .build();

    McpToolProvider toolProvider = McpToolProvider.builder()
      .mcpClients(mcpClient)
      .build();
```
