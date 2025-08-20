# Build an AI-Infused Chat Bot with LangChain4j

## Abstract

Add intelligent conversational AI in your Java application!
This presentation demonstrates how to build a production-ready chatbot from scratch using LangChain4j, progressing from basic chat functionality to a sophisticated AI assistant with memory, content moderation, document retrieval (RAG), and business integration.

## Description

Ever wondered how to add intelligent chat capabilities to your Java applications without drowning in AI complexity?
This hands-on presentation walks through building an AI-powered customer service chatbot, showcasing LangChain4j's developer-friendly approach to enterprise AI integration.

**What You'll Learn:**
- **Start Simple**: WebSocket chat integration with Quarkus
- **Add Intelligence**: Integrating an LLM for natural conversations  
- **Make It Safe**: Content moderation with a moderation AI-model to handle inappropriate messages
- **Remember Context**: Persistent chat memory using volatile memory and then Redis for multi-session conversations
- **Access Knowledge**: RAG (Retrieval Augmented Generation) with Qdrant vector database for company-specific responses
- **Connect Business**: Function calling to access live inventory, user data, and business logic

**Key Technical Highlights:**
- **Real Implementation**: Working e-commerce chatbot with actual business integration
- **Progressive Enhancement**: Each step builds naturally on the previous functionality
- **Production Ready**: Includes guardrails, error handling, memory management, and monitoring
- **Type-Safe AI**: LangChain4j's annotation-driven approach eliminates boilerplate
- **Enterprise Features**: Content moderation, persistent memory, RAG, and function calling

**Perfect For:** Java developers wanting to add AI capabilities to existing applications, architects designing conversational AI systems, and anyone curious about practical AI integration patterns.

**Live Demo Included:** See the complete chatbot in action, from basic echo responses to intelligent customer service with document retrieval and business system integration.

---

# What is LangChain4j?

- Java library for building AI-powered applications
- Simplifies integration with Large Language Models (LLMs)
- Type-safe, developer-friendly API
- Enterprise-ready with production features

---

# Key Features

- **Multiple LLM/SLM Providers**: OpenAI, Azure OpenAI, Hugging Face, Ollama, etc.
- **RAG (Retrieval Augmented Generation)**: Document ingestion and vector search
- **Function Calling**: AI agents that can execute Java methods
- **Memory Management**: Conversation history and context
- **Streaming Support**: Real-time response handling
- **Prompt Templates**: Dynamic prompt generation with variables

---

# Core Components

- **ChatLanguageModel**: Interface for LLM interactions
- **EmbeddingModel**: Text-to-vector conversion
- **VectorStore**: Storage for embeddings
- **DocumentLoader**: Ingestion of various document formats
- **MemoryStore**: Conversation persistence

---

# The VintageStore Application

---

# Basic Chat Assistant

- Set up WebSocket endpoint for real-time chat
- Basic message handling without any AI functionality
- Returns user input as-is (echo)

```java
@WebSocket(path = "/chat")
public class VintageStoreChatBot {
  private VintageStoreAssistant assistant;
  
  @OnOpen
  public String onOpen() throws Exception {
    assistant = initializeVintageStoreAssistant();
    return "Hello, how can I help you?";
  }
  
  @OnTextMessage
  public String onMessage(String message) {
    return message;
  }
}
```

---

# Adding a Chat Model

- Use Ai Services with VintageStoreAssistant
- Use AiServices to create proxy implementation
- Connect to Anthropic Claude Sonnet 4 LLM
- Enable request/response logging for debugging

```java
@SessionScoped
public interface VintageStoreAssistant {
  String chat(String userMessage);
}
```

```java
private VintageStoreAssistant initializeVintageStoreAssistant() {
  ChatModel anthropicChatModel = AnthropicChatModel.builder()
    .apiKey(ANTHROPIC_API_KEY)
    .modelName(CLAUDE_SONNET_4_20250514.toString())
    .temperature(0.3)
    .timeout(ofSeconds(60))
    .logRequests(true)
    .logResponses(true)
    .build();

  return AiServices.builder(VintageStoreAssistant.class)
    .chatModel(anthropicChatModel)
    .build();
}
```

---

# Adding System Prompt

- Define AI assistant personality and role
- Provide context about VintageStore business
- Set behavioral guidelines (polite, helpful)
- Use `@SystemMessage` and `@UserMessage` annotations

```java
@SessionScoped
public interface VintageStoreAssistant {
  
  @SystemMessage("""
    You are a helpful customer support assistant for VintageStore.
    VintageStore is an online store selling vintage books and CDs.
    Always be polite and helpful to customers.
    """)
  String chat(@UserMessage String userMessage);
}
```

---

# Moderating Content

- Add Mistral AI moderation model for safety
- Detect harmful content (hate, discrimination, etc.)
- Use `@Moderate` annotation to enable filtering
- Automatically blocks inappropriate messages

```java
// Initialize moderation model
ModerationModel mistralModerationModel = new MistralAiModerationModel.Builder()
  .apiKey(MISTRAL_AI_API_KEY)
  .modelName(MISTRAL_MODERATION_LATEST.toString())
  .logRequests(true)
  .logResponses(true)
  .build();

// Add moderation to AI Services
VintageStoreAssistant assistant = AiServices.builder(VintageStoreAssistant.class)
  .chatModel(anthropicChatModel)
  .moderationModel(mistralModerationModel)
  .build();
```

```java
@Moderate
String chat(@UserMessage String userMessage);
```

---

# Handling Moderation Exceptions

- Catch `ModerationException` when harmful content detected
- Log moderation details for analysis
- Return friendly message to redirect to human support
- Graceful handling without exposing internal errors

```java
@OnTextMessage
public String onMessage(String message) throws Exception {
  try {
    return assistant.chat(message);
  } catch (ModerationException e) {
    LOG.warn("Moderation triggered: " + e.moderation());
    return "I will redirect you to a human assistant...";
  }
}
```

---

# Adding Memory

- Enable conversation context with `MessageWindowChatMemory`
- Keep last 20 messages for context
- AI remembers previous conversation within session
- **Problem**: Memory lost when WebSocket connection closes!

```java
// In-memory chat memory
ChatMemory chatMemory = MessageWindowChatMemory.builder()
  .maxMessages(20)
  .build();

VintageStoreAssistant assistant = AiServices.builder(VintageStoreAssistant.class)
  .chatModel(anthropicChatModel)
  .chatMemory(chatMemory)
  .build();
```

---

# Adding Persistent Memory with Redis

- Store conversation history in Redis database
- Memory survives WebSocket disconnections
- Use `ChatMemoryProvider` for dynamic memory creation
- Each session gets persistent conversation context

```java
// Initialize Redis chat memory store
ChatMemoryStore redisChatMemoryStore = RedisChatMemoryStore.builder()
  .host("localhost")
  .port(6379)
  .build();

ChatMemoryProvider redisChatMemoryProvider = memoryId -> 
  MessageWindowChatMemory.builder()
    .maxMessages(20)
    .chatMemoryStore(redisChatMemoryStore)
    .build();

// Use memory provider instead of direct memory
VintageStoreAssistant assistant = AiServices.builder(VintageStoreAssistant.class)
  .chatModel(anthropicChatModel)
  .chatMemory(chatMemory)
  .chatMemoryProvider(redisChatMemoryProvider)
  .build();
```

---

# Handling Multiple User Sessions

- Support concurrent users with separate conversations
- Use WebSocket connection ID as unique session identifier
- Add `@MemoryId` parameter to AI service interface
- Each user maintains independent conversation history

```java
@Inject
WebSocketConnection webSocketConnection;

// Use connection ID as memory identifier
public interface VintageStoreAssistant {
  String chat(@MemoryId String sessionId, @UserMessage String userMessage);
}

// Pass connection ID to chat method
return assistant.chat(webSocketConnection.id(), message);
```

```java
ChatMemoryProvider redisChatMemoryProvider = memoryId -> 
  MessageWindowChatMemory.builder()
    .id(webSocketConnection.id())  // Unique per connection
    .maxMessages(20)
    .chatMemoryStore(redisChatMemoryStore)
    .build();
```

---

# RAG - Injecting VintageStore Documents in Qdrant

- Parse PDF documents (Terms & Conditions, user manuals)
- Split documents into chunks (1000 chars, 200 overlap)
- Generate embeddings using AllMiniLM-L6-V2 model
- Store in Qdrant vector database for semantic search

```java
// DocumentIngestor.java - Ingest PDFs into Qdrant
ApachePdfBoxDocumentParser pdfParser = new ApachePdfBoxDocumentParser();
Document document = pdfParser.parse(Files.newInputStream(pdfFile));

// Split document into segments
DocumentSplitter splitter = DocumentSplitters.recursive(1000, 200);
List<TextSegment> segments = splitter.split(document);

// Convert to embeddings
EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

// Store in Qdrant
embeddingStore.addAll(embeddings, segments);
```

---

# RAG - Retrieving Content from Qdrant

- Connect to Qdrant vector database
- Set up embedding store with VintageStore collection
- Create content retriever for semantic search
- Same embedding model as ingestion for consistency

```java
// Initialize Qdrant client
QdrantClient qdrantClient = new QdrantClient(
  QdrantGrpcClient.newBuilder(QDRANT_HOST, QDRANT_PORT, false).build()
);

// Create embedding store
EmbeddingStore qdrantEmbeddingStore = QdrantEmbeddingStore.builder()
  .client(qdrantClient)
  .collectionName(QDRANT_COLLECTION)
  .build();

// Create content retriever
ContentRetriever qdrantContentRetriever = new EmbeddingStoreContentRetriever(
  qdrantEmbeddingStore, 
  new AllMiniLmL6V2EmbeddingModel()
);
```

---

# Adding RAG to AI Services

- Integrate content retriever with AI Services
- AI automatically searches relevant documents
- Contextual answers based on company knowledge
- **Test**: Ask about Terms and Conditions, cookies, VAT

```java
VintageStoreAssistant assistant = AiServices.builder(VintageStoreAssistant.class)
  .chatModel(anthropicChatModel)
  .moderationModel(mistralModerationModel)
  .chatMemoryProvider(redisChatMemoryProvider)
  .contentRetriever(qdrantContentRetriever)  // Add RAG
  .build();
```

**Now the AI can answer**: "What are the Terms and Conditions?"

---

# Document Processing

- **Supported Formats**: PDF, DOCX, TXT, HTML, Markdown
- **Text Splitters**: Sentence, paragraph, token-based
- **Preprocessing**: Clean, normalize, extract metadata

```java
DocumentSplitter splitter = DocumentSplitters.recursive(300, 30);
List<TextSegment> segments = splitter.split(document);
```

---

# Vector Stores

- **In-Memory**: Quick prototyping
- **Chroma**: Open-source vector database  
- **Pinecone**: Managed vector service
- **Weaviate**: GraphQL-based vector search
- **Qdrant**: High-performance vector engine

---

# Adding Tools (Function Calling) to Access Business Logic

- Enable AI to call Java methods as functions
- Access live business data (inventory, user profiles)
- Execute real business operations through AI
- **Test**: "Give me user details", "Find Java books", "Top-rated CDs"

```java
// Tools for business logic
.tools(
  new LegalDocumentTools(),     // Access legal documents
  new ItemsInStockTools(),      // Search inventory
  new UserLoggedInTools()       // User information
)
```

**Now the AI can**:
- Get user details
- Search for books and CDs
- Access business data

---

# Final Architecture

```java
VintageStoreAssistant assistant = AiServices.builder(VintageStoreAssistant.class)
  .chatModel(anthropicChatModel)           // LLM
  .moderationModel(mistralModerationModel) // Safety
  .chatMemoryProvider(redisChatMemoryProvider) // Memory
  .contentRetriever(qdrantContentRetriever)    // RAG
  .tools(new LegalDocumentTools(),             // Tools
         new ItemsInStockTools(), 
         new UserLoggedInTools())
  .build();
```

---

# Resources

- **Documentation**: https://docs.langchain4j.dev
- **GitHub**: https://github.com/langchain4j/langchain4j
- **Examples**: https://github.com/langchain4j/langchain4j-examples
- **Community**: Discord, Stack Overflow

---

# Questions?

Thank you for your attention!

- GitHub: @langchain4j
- Documentation: docs.langchain4j.dev
- Community Discord: [invite link]
