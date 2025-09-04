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
* In Intellij IDEA:
  * Uncheck `optimize imports on the fly`
  * Copilot Disable Completion
* Open several terminals in the IDE `web`, `rag`, `mcp`
* Open 2 different Browsers (eg. Edge and Firefox)
* In `VintageStoreAssistant` just leave the following code:

```java
@SessionScoped
public interface VintageStoreAssistant {

  String chat(@UserMessage String userMessage);
}
```

* In `VintageStoreChatBot`:
  * Remove `@Inject WebSocketConnection webSocketConnection;`
  * Remove the entire method `initializeVintageStoreAssistant`
  * Change the code of the WebSocket to the following:

```java
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

  long startTime = System.currentTimeMillis();
  String response = message;
  logInvocation(startTime, null);
  return response;
}

@OnClose
public void onClose() {
  LOG.info("WebSocket chat connection closed");
}
```

## 01 - Show the VintageStore application

* Start PostgreSQL database (`docker compose -p vintagestore -f infrastructure/docker/postgresql.yml up`) and Quarkus (`mvn quarkus:dev`)
* Start Quarkus in dev mode with `mvn quarkus:dev`
* Go to http://localhost:8080
* Browse CD and Books
* Show Terms and Conditions
* Login/Profile/Logout
* Show logs
* 🧠 "Hi"
* 🧠 "Echo"
* CLEAR CONVERSATION / disconnect / connect
* Show the code `VintageStoreAssistant` and `VintageStoreChatBot`
* => I want to add a chat bot to the VintageStore application

## 10 - Add an LLM to the Chat Bot (lc-llm)

* In `VintageStoreChatBot`  execute live template `lc-llm`
* Add `assistant = initializeVintageStoreAssistant();` in the `@OnOpen` method
* Add `String response = assistant.chat(message);` to the `@OnTextMessage` method
* Restart Quarkus (press 's' in the terminal)
* 🧠 "Hi"
* 🧠 "What is the capital of France ?"
* Show logs and check the LLM calls (look for `"content"` in the logs)
* This is totally useless for VintageStore, so we will add a system prompt
* 🧠 "Do you know anything about VintageStore ?"

## 11 - Add a System Prompt  (lc-prompt)

* In `VintageStoreAssistant` add the system message executing `lc-prompt`
* Read the system message
* Restart Quarkus (press 's' in the terminal)
* In Intellij IDEA Quarkus terminal clear the logs with `CMD + K`
* Disconnect and connect the chat websocket
* 🧠 "Do you know anything about VintageStore ?"
* 🧠 "What is the capital of France ?"
* 🧠 "What's the day today?'"
* Show logs and check the system prompt (look for `"system"`) and look for `The current date is`
* 🧠 "I HATE YOU AND YOUR WEBSITE"

## 12 - Moderation (lc-moderate)

* In `VintageStoreChatBot` add the moderation model running `lc-moderate`
* Add `.moderationModel(mistralModerationModel)`
* Show the text of the `MODERATION_PROMPT`
* Add `@Moderate` in `VintageStoreAssistant`
* Restart Quarkus (press 's' in the terminal)
* In Intellij IDEA Quarkus terminal clear the logs with `CMD + K`
* Disconnect and connect the chat websocket
* 🧠 "I HATE YOU AND YOUR WEBSITE"
* Show the logs and look for `hate_and_discrimination`
* => LLM has no memory
* 🧠 "What's my name ?"
* 🧠 "My name is Antonio"
* 🧠 "What's my name ?"

## 20 - Memory (lc-memory)

* REMOVE MODERATION BECAUSE IT WILL CLASH WITH MEMORY `//@Moderate`
* In `VintageStoreChatBot` add the memory by running `lc-memory`
* Add `.chatMemory(chatMemory)`
* Restart Quarkus (press 's' in the terminal)
* Disconnect and connect the chat websocket
* 🧠 "What's my name ?"
* 🧠 "My name is Antonio"
* 🧠 "What's my name ?"
* Show the logs and check the memory (look for `"What's my name ?"` in the logs)
* DISCONNECT AND CONNECT WEBSOCKET, MEMORY IS LOST
* 🧠 "What's my name ?"
* => Context is lost because memory is not persistent

## 21 - Memory in Persistent Storage (lc-redis)

* Start Redis with `docker compose -p vintagestore -f infrastructure/docker/redis.yml up`
* Show the Redis Commander http://localhost:8089
* Remove `ChatMemory` and execute `lc-redis`
* Restart Quarkus (press 's' in the terminal)
* Disconnect and connect the chat websocket
* 🧠 "What's my name ?"
* 🧠 "My name is Antonio"
* 🧠 "What's my name ?"
* Disconnect and connect the chat websocket
* 🧠 "What's my name ?"
* Show the Redis Commander and copy the content to the logs.json
* Now CLEAR THE CONVERSATION in the chat
* Implement `redisChatMemoryStore.deleteMessages("default");` in the `@OnTextMessage` method
* Implement `redisChatMemoryStore.deleteMessages("default");` in the `@OnClose` method
* In Chrome
  * 🧠 "My name is Antonio"
  * 🧠 "What's my name ?"
* In Firefox
  * 🧠 "What's my name ?"
  * 🧠 "No, my name is Maria"
* => One unique conversation is stored 

## 22 - Multiple User Chat History

* Show the discussion in Redis
* Add `@Inject WebSocketConnection webSocketConnection;`
* Add memory id `String chat(@MemoryId String sessionId, @UserMessage String userMessage);`
* Add connection id to logs `LOG.info("WebSocket chat connection opened with ID: " + webSocketConnection.id());`
* Add connection id to chat `return assistant.chat(webSocketConnection.id(), message);`
* Add connection id to remove `redisChatMemoryStore.deleteMessages(webSocketConnection.id());` in `@OnClose` and `@OnTextMessage`
* Add connection id to ChatMemoryProvider `.id(webSocketConnection.id())`
* Restart Quarkus (press 's' in the terminal)
* In Chrome
  * 🧠 "My name is Antonio"
  * 🧠 "What's my name ?"
* In Firefox
  * 🧠 "What's my name ?"
  * 🧠 "No, my name is Maria"
* Show the 2 discussions in Redis
* Clear one conversation and show Redis
* 🧠 "What are the Terms and Conditions of VintageStore ?"
* 🧠 "What is your VAT number ?"

## 30 - RAG (lc-rag)

* Show the PDFs in the web application and show that T&C are there
* Start Qdrant with `docker compose -p vintagestore -f infrastructure/docker/qdrant.yml up`
* Show Qdrant dashboard with no collection (http://localhost:6333/dashboard)
* Show code in `DocumentIngestor`
* `cd rag` and execute `mvn compile exec:java`
* Show Qdrant dashboard with `VintageStore` collection and show the text segments
* Add the code for the `EmbeddingStore` with `lc-rag`
* Add `.contentRetriever(qdrantContentRetriever)`
* Move the `qdrantClient.close();` code to the `@OnClose`
* Restart Quarkus (press 's' in the terminal)
* Disconnect and connect the chat websocket
* 🧠 "What are the Terms and Conditions of VintageStore ?"
* 🧠 "What is your VAT number ?"
* 🧠 "What are the currencies I can pay with ?"
* Show the logs
* But we need to access tools
* 🧠 "Give me all my user details"
* 🧠 "Do you have any book on Java ?"
* 🧠 "What are the top-rated CDs ?"

## 40 - Tools (lc-tools)

* Show the code of the Tools `LegalDocumentTools`, `ItemsInStockTools`, `UserLoggedInTools`
* Add the tools with `lc-tools`
* Restart Quarkus (press 's' in the terminal)
* Disconnect and connect the chat websocket
* 🧠 "Give me all my user details"
* 🧠 "When were the terms and condition updated?"
* 🧠 "What are the top-rated CDs ?"
* But we need an external service to convert to Euros
* 🧠 "You use dollars. But how much are the CDs in Euros ?"
* => We need to access an external service

## 41 - MCP (lc-mcp)

* Show the code of the `MCPServerCurrency`
* Add the MCP client in `VintageStoreChatBot` with `lc-mcp`
* Add the MCP to the assistant with `.toolProvider(toolProvider)`
* Restart Quarkus (press 's' in the terminal)
* Disconnect and connect the chat websocket
* 🧠 "What are the top-rated CDs ?"
* 🧠 "How much are the CDs in Euros ?"
* Show the log `dollars to euros:`
* 🧠 "Any books on Java?"
* 🧠 "Any books on Python?"
* => Too many tokens sent

## 50 - Token consumption (lc-token)

* Replace method signature `Result<String> chat(@MemoryId String sessionId, @UserMessage String userMessage);`
* Return the content of the response `return response.content()`
* Pass the token usage `logInvocation(startTime, response.tokenUsage());`
* Remove logs `IS_LOGGING_ENABLED = false`
* Restart Quarkus (press 's' in the terminal)
* Disconnect and connect the chat websocket
* Sign-in as `john.doe`
* 🧠 "Hi"
* 🧠 "What are my profile details?"
* 🧠 "Any books on Java?"
* 🧠 "What are the top rated CDs ?"
* Show the `rate_limit_error`
* DONT USE CHAT TO SEARCH CATALOG

## 51 - Summarizing conversation (lc-sum)

* Show the code of `SummarizingTokenWindowChatMemory`
* In `VintageStoreChatBot` replace `MessageWindowChatMemory` with `SummarizingTokenWindowChatMemory`
* Add the summary model with `lc-sum`
* Sign-in as `john.doe`
* 🧠 "Hi"
* 🧠 "I like the colour black"
* 🧠 "What are my profile details?"
* 🧠 "Any books on Java?"
* 🧠 "What are the top rated CDs ?"
* 🧠 "What is my favourite colour ?"

## Monitoring

## Guardrails

* User inputs is too long

# Final code

```java

@SessionScoped
public interface VintageStoreAssistant {

  @SystemMessage("""
    You are the official customer service chatbot for **Vintage Store**. Your primary role is to assist customers with inquiries related to our products, services, policies, and shopping experience.
    
    The current date is {{current_date}}
    
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
  Result<String> chat(@MemoryId String sessionId, @UserMessage String userMessage);
}
```

```java
package org.agoncal.application.vintagestore.chat;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.cohere.CohereEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.mistralai.MistralAiModerationModel;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
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
    LOG.info("WebSocket chat connection closed for ID: " + webSocketConnection.id());
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

    if (duration < 2000) {
      LOG.info(GREEN + message + RESET);
    } else if (duration <= 5000) {
      LOG.warn(ORANGE + message + RESET);
    } else {
      LOG.error(RED + message + RESET);
    }
  }

  private VintageStoreAssistant initializeVintageStoreAssistant() {

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

    // Initialize the summary model
    ChatModel openAiSummarizationModel = OpenAiChatModel.builder()
      .apiKey(OPENAI_API_KEY)
      .modelName(GPT_4_1_MINI)
      .logRequests(IS_LOGGING_ENABLED)
      .logResponses(IS_LOGGING_ENABLED)
      .build();

    // Initialize the memory
    redisChatMemoryStore = RedisChatMemoryStore.builder()
      .host("localhost")
      .port(6379)
      .build();

    ChatMemoryProvider redisChatMemoryProvider = memoryId -> SummarizingTokenWindowChatMemory.builder()
      .id(webSocketConnection.id())
      .maxTokens(MAX_TOKENS, new OpenAiTokenCountEstimator(GPT_4_1_MINI))
      .summarizer(new OpenAISummarizer((OpenAiChatModel) openAiSummarizationModel, TOKEN_LIMIT))
      .chatMemoryStore(redisChatMemoryStore)
      .build();

    // Initialize the embedding model and embedding store
    qdrantClient = new QdrantClient(QdrantGrpcClient.newBuilder(QDRANT_HOST, QDRANT_PORT, false).build());

    QdrantEmbeddingStore qdrantEmbeddingStore = QdrantEmbeddingStore.builder()
      .client(qdrantClient)
      .collectionName(QDRANT_COLLECTION)
      .build();

    ContentRetriever qdrantContentRetriever = new EmbeddingStoreContentRetriever(qdrantEmbeddingStore, cohereEmbeddingModel);

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
      .chatModel(anthropicChatModel)
      .moderationModel(mistralModerationModel)
      .chatMemoryProvider(redisChatMemoryProvider)
      .contentRetriever(qdrantContentRetriever)
      .tools(new LegalDocumentTools(), new ItemsInStockTools(), new UserLoggedInTools())
      .toolProvider(mcpToolProvider)
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
    .logRequests(IS_LOGGING_ENABLED)
    .logResponses(IS_LOGGING_ENABLED)
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
  .logRequests(IS_LOGGING_ENABLED)
  .logResponses(IS_LOGGING_ENABLED)
  .build();

  try{
      // 
  } catch(ModerationException e) {
    LOG.warn("/!\\ The customer is not happy /!\\ "+message +" - "+e.moderation());
    return MODERATION_PROMPT;
  }
```

lc-prompt - LangChain4j Demo - Add a System Prompt

```java
  @SystemMessage("""
  You are the official customer service chatbot for **Vintage Store**. Your primary role is to assist customers with inquiries related to our products, services, policies, and shopping experience.
  
  The current date is {{current_date}}
  
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
// Initialize the embedding model
EmbeddingModel cohereEmbeddingModel = CohereEmbeddingModel.builder()
  .apiKey(COHERE_API_KEY)
  .modelName(COHERE_EMBED_ENGLISH)
  .inputType("search_document")
  .logRequests(IS_LOGGING_ENABLED)
  .logResponses(IS_LOGGING_ENABLED)
  .build();

// Initialize the embedding model and embedding store
qdrantClient =new QdrantClient(QdrantGrpcClient.newBuilder(QDRANT_HOST, QDRANT_PORT, false).build());

QdrantEmbeddingStore qdrantEmbeddingStore = QdrantEmbeddingStore.builder()
  .client(qdrantClient)
  .collectionName(QDRANT_COLLECTION)
  .build();

ContentRetriever qdrantContentRetriever = new EmbeddingStoreContentRetriever(qdrantEmbeddingStore, cohereEmbeddingModel);

// MOVE TO @ONCLOSE
if(qdrantClient !=null){
  LOG.info("Closing Qdrant client connection");
  qdrantClient.close();
}
```

lc-redis - langChain4j Demo - Adds Redis

```java
// Initialize the memory
redisChatMemoryStore =RedisChatMemoryStore.builder()
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

lc-mcp - LangChain4j Demo - Adds MCP Client

```java
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
```

lc-sum - LangChain4j Demo - Adds the Summarization

```java
// Initialize the summary model
ChatModel openAiSummarizationModel = OpenAiChatModel.builder()
  .apiKey(OPENAI_API_KEY)
  .modelName(GPT_4_1_MINI)
  .logRequests(IS_LOGGING_ENABLED)
  .logResponses(IS_LOGGING_ENABLED)
  .build();

.maxTokens(MAX_TOKENS, new OpenAiTokenCountEstimator(GPT_4_1_MINI))
.summarizer(new OpenAISummarizer((OpenAiChatModel) openAiSummarizationModel,TOKEN_LIMIT))
```
