# Vintage Store

An AI-powered e-commerce application built with **Quarkus** that demonstrates sophisticated chat capabilities using **Retrieval Augmented Generation (RAG)** and real-time WebSocket communication.

## Features

- 🛍️ **E-commerce Platform**: Browse and search books and CDs with comprehensive metadata
- 🤖 **AI Chat Assistant**: Intelligent chat bot powered by Claude Sonnet with RAG capabilities
- 📚 **Document Knowledge Base**: AI can answer questions about terms and conditions from PDF documents
- 🔍 **Real-time Inventory Queries**: Chat bot can search and recommend products from the store inventory
- 💾 **Persistent Chat Memory**: Conversation history stored in Redis
- 🌐 **WebSocket Integration**: Real-time chat communication with Bootstrap 5 UI

## Technology Stack

- **Quarkus Renarde** - Web framework with type-safe Qute templating
- **LangChain4j** - AI integration with OpenAI GPT-4o and RAG capabilities
- **Hibernate ORM with Panache** - Simplified data persistence
- **H2 Database** - In-memory database with comprehensive test data (99 books, 101 CDs)
- **Qdrant Vector Database** - Document embeddings storage (384-dimensional)
- **Redis** - Chat memory persistence
- **Bootstrap 5** - Frontend UI framework

## Quick Start

### Prerequisites

1. **Start External Services** (required for full functionality):
```bash
# Start Qdrant vector database (required for AI chat)
docker-compose -f src/main/docker/qdrant.yml up -d

# Start Redis for chat memory persistence  
docker-compose -f src/main/docker/redis.yml up -d

# Set ANTHROPIC_API_KEY API key for chat functionality
export ANTHROPIC_API_KEY=your_api_key_here
```

2. **Ingest Documents** (for RAG system):
```bash
./mvnw exec:java  # Processes PDFs in /static/terms/
```

### Running the Application

Start the development server with live reload:
```bash
./mvnw compile quarkus:dev
```

Access the application at: **http://localhost:8080**

> **Dev UI**: Available at <http://localhost:8080/q/dev/> for debugging and monitoring.

## Application Features

### E-commerce Functionality
- **Books**: Browse 99 books with categories, publishers, authors, ISBN, and publication details
- **CDs**: Explore 101 CDs with genres, labels, musicians, and disc information  
- **Search & Filter**: Find items by title, author, musician, category, or genre
- **REST API**: JSON endpoints for programmatic access to inventory

### AI Chat System
- **Smart Assistant**: Ask questions about products, get recommendations, check availability
- **Document Q&A**: Query terms and conditions, privacy policies, and other PDF documents
- **Memory Persistence**: Conversations remembered across sessions
- **Function Calling**: Real-time inventory queries during chat conversations

### Data Architecture
- **Single-table inheritance** with discriminator pattern for Items (Books/CDs)
- **Many-to-many relationships** via junction tables (Book-Author, CD-Musician)
- **Comprehensive test data** loaded via `import.sql` (300KB+ dataset)

## API Endpoints

The application provides REST APIs for all entities:
- `/books`, `/books/categories`, `/books/publishers`, `/books/authors`
- `/cds`, `/cds/genres`, `/cds/labels`, `/cds/musicians`

OpenAPI documentation available at `/q/swagger-ui/`

## Testing

```bash
./mvnw test          # Run unit tests
./mvnw verify        # Run integration tests
```

## Build & Deployment

### Standard JAR
```bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

### Uber JAR
```bash
./mvnw package -Dquarkus.package.jar.type=uber-jar
java -jar target/*-runner.jar
```

### Native Executable
```bash
./mvnw package -Dnative
./target/vintagestore-1.0.0-SNAPSHOT-runner
```

### Container-based Native Build
```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

## Architecture Overview

### Package Structure
- `org.agoncal.application.vintagestore.web` - Renarde controllers and static resources
- `org.agoncal.application.vintagestore.api` - REST API endpoints  
- `org.agoncal.application.vintagestore.model` - Panache entities and data model
- `org.agoncal.application.vintagestore.chat` - WebSocket chat implementation
- `org.agoncal.application.vintagestore.rag` - Document ingestion and RAG components

### Template Engine
- **Qute templating** with Renarde for type-safe templates
- **Bootstrap 5** responsive design with integrated chat sidebar
- **WebSocket integration** for real-time chat communication

## Related Resources

- [Quarkus Framework](https://quarkus.io/) - Supersonic Subatomic Java Framework
- [Quarkus Renarde](https://quarkiverse.github.io/quarkiverse-docs/quarkus-renarde/dev/) - Web framework with server-side rendering
- [LangChain4j](https://docs.langchain4j.dev/) - Java library for building AI applications
- [Qdrant](https://qdrant.tech/) - Vector similarity search engine
- [OpenAI API](https://platform.openai.com/docs/) - GPT models and embeddings
