# Vintage Store

A modular AI-powered e-commerce application demonstrating sophisticated chat capabilities using **Retrieval Augmented Generation (RAG)** and real-time WebSocket communication. The project is organized into four distinct modules showcasing different approaches to building AI-enabled applications with Quarkus and LangChain4j.

## Features

- 🛍️ **E-commerce Platform**: Browse and search books and CDs with comprehensive metadata
- 🤖 **AI Chat Assistant**: Intelligent chat bot powered by Claude Sonnet 4 with RAG capabilities
- 📚 **Document Knowledge Base**: AI can answer questions about terms and conditions from PDF documents
- 🔍 **Real-time Inventory Queries**: Chat bot can search and recommend products from the store inventory
- 💾 **Persistent Chat Memory**: Conversation history stored in Redis with clear conversation functionality
- 🌐 **Enhanced WebSocket Chat**: sidebar with Markdown rendering, clear history button
- 👤 **User Authentication**: Complete signin/logout system with role-based access (USER/ADMIN)
- 📊 **Admin Dashboard**: User management interface with statistics and role-based features
- 🎨 **Modern UI**: Dark theme navigation with Bootstrap 5, responsive design, user dropdown

## Technology Stack

- **Quarkus Renarde** - Web framework with type-safe Qute templating
- **LangChain4j** - AI integration with Anthropic Claude Sonnet 4 and RAG capabilities
- **Hibernate ORM with Panache** - Simplified data persistence
- **PostgreSQL 17.5** - Production database with comprehensive test data (99 books, 101 CDs)
- **Qdrant Vector Database** - Document embeddings storage (384-dimensional)
- **Redis** - Chat memory persistence
- **Bootstrap 5** - Frontend UI framework

## Quick Start

### Prerequisites

1. **Start External Services** (required for full functionality):
```bash
# Start PostgreSQL database
docker compose -p vintagestore -f infrastructure/docker/postgresql.yml up -d

# Start Qdrant vector database (required for AI chat)
docker compose -p vintagestore -f infrastructure/docker/qdrant.yml up -d

# Start Redis for chat memory persistence  
docker compose -p vintagestore -f infrastructure/docker/redis.yml up -d

# Set ANTHROPIC_API_KEY API key for chat functionality
export ANTHROPIC_API_KEY=your_api_key_here
```

2. **Ingest Documents** (for RAG system):
```bash
cd rag
./mvnw exec:java  # Processes PDFs in /src/main/resources/terms/
```

### Running the Application

Choose one of the two web application implementations:

**Option 1: LangChain4j standalone** (web-langchain4j):
```bash
cd web-langchain4j
./mvnw compile quarkus:dev
```

**Option 2: Quarkus LangChain4j extension** (web-quarkus):
```bash
cd web-quarkus  
./mvnw compile quarkus:dev
```

Access the application at: **http://localhost:8080**

> **Dev UI**: Available at <http://localhost:8080/q/dev/> for debugging and monitoring.

### Stop the application:

```bash
docker compose -p vintagestore down
```

## Comparing the Two Web Applications

Both `web-langchain4j` and `web-quarkus` provide **identical functionality** but demonstrate different approaches to AI integration:

### **web-langchain4j** (Standalone LangChain4j)
- **Manual configuration** of LangChain4j components
- **Direct dependency management** for AI models and tools
- **Explicit bean configuration** for chat assistants and memory
- **Full control** over AI integration setup
- **Learning approach** for understanding LangChain4j internals

### **web-quarkus** (Quarkus LangChain4j Extension)
- **Simplified configuration** via Quarkus extension
- **Automatic bean registration** and dependency injection
- **Configuration-driven setup** through application.properties
- **Reduced boilerplate** code for AI integration
- **Production-ready approach** with Quarkus ecosystem benefits

Both applications include:
- ✅ **Same UI/UX** - Identical templates and Bootstrap styling
- ✅ **Same features** - Authentication, chat, inventory, admin panel
- ✅ **Same data model** - Books, CDs, users with full relationships
- ✅ **Same AI capabilities** - RAG, function calling, memory persistence
- ✅ **Same performance** - Both leverage Quarkus optimizations

> **Recommendation**: Use `web-quarkus` for production applications to benefit from the Quarkus ecosystem's configuration management and developer experience.

## RAG System & Document Ingestion

The **RAG (Retrieval Augmented Generation)** module provides the knowledge base for the AI chat system:

### Document Processing Pipeline
1. **PDF Documents** - Located in `rag/src/main/resources/terms/`:
   - `terms.pdf` - Terms and conditions
   - `privacy.pdf` - Privacy policy
   - `acceptable-use-policy.pdf` - Usage guidelines
   - `disclaimer.pdf` - Legal disclaimers
   - `end-user-license-agreement.pdf` - EULA

2. **DocumentIngestor** - Processes documents through:
   - **Text extraction** from PDF files
   - **Text chunking** with overlapping segments for context preservation
   - **Embedding generation** using AllMiniLM-L6-V2 model (384 dimensions)
   - **Vector storage** in Qdrant database with metadata

3. **Query Processing** - During chat interactions:
   - **Semantic search** against document embeddings
   - **Context retrieval** for relevant document sections
   - **RAG integration** with Claude Sonnet 4 for accurate responses

### Running Document Ingestion
```bash
# Ensure Qdrant is running
docker compose -p vintagestore -f infrastructure/docker/qdrant.yml up -d

# Process documents into vector database
cd rag
./mvnw exec:java

# Verify ingestion (should show processed documents)
curl http://localhost:6333/collections/documents/points/count
```

### RAG Configuration
- **Vector dimensions**: 384 (AllMiniLM-L6-V2 model)
- **Chunk size**: Optimized for legal document structure
- **Overlap strategy**: Preserves context across document sections
- **Metadata storage**: Document source, chunk position, and content type

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

### User Authentication System
- **Complete signin/logout** functionality with session management
- **Role-based access control** (USER vs ADMIN roles)
- **User profile pages** with personalized features
- **Admin user management** interface at `/view/users`
- **20 test users** included in dataset (16 USER, 4 ADMIN roles)
- **Session-scoped UserSession** CDI bean for state management
- **Template globals** for user state access across all templates

### Enhanced Chat Features
- **Markdown support** in chat messages via Marked.js library
- **Clear conversation** functionality with refresh button
- **Wide sidebar** for better readability
- **User authentication integration** - chat assistant knows if user is logged in
- **Enhanced typography** with improved font sizes and spacing
- **CLEAR_CONVERSATION WebSocket command** for memory reset

### Data Architecture
- **Single-table inheritance** with discriminator pattern for Items (Books/CDs)
- **Many-to-many relationships** via junction tables (Book-Author, CD-Musician)
- **User management tables** with authentication and role-based access
- **Comprehensive test data** loaded via `vintagestore-data.sql` (300KB+ dataset) into PostgreSQL
- **Author birth dates** with age calculation using modern Java time APIs

### Demo Accounts
The application includes test user accounts for different roles:

**Admin Account:**
- Username: `admin`
- Password: `adminpass`
- Role: ADMIN (access to user management)

**User Account:**
- Username: `john.doe`
- Password: `password123`  
- Role: USER (standard access)

### Authentication Features
- **Sign in/Sign out** functionality
- **Role-based UI** (different menus for USER vs ADMIN)
- **User profile pages** with account information
- **Admin user management** interface
- **Session management** with CDI beans
- **Template integration** - user state available across all pages

## Testing

Run tests for each module individually:

```bash
# Test RAG module
cd rag
./mvnw test

# Test web-langchain4j module  
cd web-langchain4j
./mvnw test          # Run unit tests
./mvnw verify        # Run integration tests

# Test web-quarkus module
cd web-quarkus  
./mvnw test          # Run unit tests
./mvnw verify        # Run integration tests
```

## Build & Deployment

Choose your preferred web application module and build accordingly:

### Standard JAR
```bash
# For web-langchain4j
cd web-langchain4j
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar

# For web-quarkus  
cd web-quarkus
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

### Uber JAR
```bash
cd web-langchain4j  # or web-quarkus
./mvnw package -Dquarkus.package.jar.type=uber-jar
java -jar target/*-runner.jar
```

### Native Executable
```bash
cd web-langchain4j  # or web-quarkus
./mvnw package -Dnative
./target/vintagestore-1.0.0-SNAPSHOT-runner
```

### Container-based Native Build
```bash
cd web-langchain4j  # or web-quarkus
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

## Project Structure

The project is organized into **four distinct modules**, each serving a specific purpose:

### 📁 **infrastructure/**
Contains all external service configurations:
- **Docker Compose files** for PostgreSQL, Qdrant, and Redis
- **Database initialization scripts** with schema and permissions setup
- **Shared infrastructure components** used by all modules

### 📁 **rag/**
Standalone module for **document ingestion and RAG system**:
- **DocumentIngestor.java** - Processes PDF documents into vector embeddings
- **AllMiniLM-L6-V2 model** for embedding generation (384-dimensional vectors)
- **Qdrant integration** for storing and querying document embeddings
- **Terms and conditions PDFs** processed for knowledge base

### 📁 **web-langchain4j/**
**Complete web application using LangChain4j standalone**:
- **Quarkus Renarde** framework with type-safe Qute templating
- **Direct LangChain4j integration** without Quarkus extension
- **Full e-commerce functionality** with books and CDs catalog
- **AI chat system** with WebSocket communication
- **User authentication** and role-based access control
- **Bootstrap 5 UI** with modern responsive design

### 📁 **web-quarkus/**
**Identical web application using Quarkus LangChain4j extension**:
- **Same functionality** as web-langchain4j module
- **Quarkus LangChain4j extension** for simplified AI integration
- **Demonstrates extension benefits** vs standalone approach
- **Identical UI and features** for direct comparison

## Architecture Overview

### Core Technology Stack
- **Quarkus 3.24.5** with Renarde web framework
- **LangChain4j 1.1.0** for AI integration (both standalone and extension approaches)
- **Anthropic Claude Sonnet 4** as the AI model
- **Hibernate ORM with Panache** for data persistence
- **PostgreSQL 17.5** with comprehensive test data
- **Qdrant Vector Database** for RAG document storage
- **Redis** for chat memory persistence
- **Bootstrap 5** for modern responsive UI

### Package Structure (Both Web Modules)
- `org.agoncal.application.vintagestore.chat` - WebSocket chat implementation and AI tools
- `org.agoncal.application.vintagestore.model` - Panache entities and data model
- `org.agoncal.application.vintagestore.web` - Renarde controllers and static resources

### Template Engine & UI
- **Qute templating** with Renarde for type-safe templates
- **Modern Bootstrap 5** design with dark theme navigation
- **Enhanced chat sidebar** (700px wide) with Markdown support
- **Responsive design** with role-based UI elements
- **User authentication templates** (signin, profile, user management)
- **WebSocket integration** for real-time chat communication
- **Template globals** for user state access (login status, admin rights)

### Logging & Monitoring
- **Comprehensive logging** across all controllers and API endpoints
- **Method entry logging** for debugging and monitoring
- **Authentication event logging** (login attempts, successes, failures)
- **User activity tracking** via application logs
- **JBoss Logging** integration with meaningful log messages

## Related Resources

- [Quarkus Framework](https://quarkus.io/) - Supersonic Subatomic Java Framework
- [Quarkus Renarde](https://quarkiverse.github.io/quarkiverse-docs/quarkus-renarde/dev/) - Web framework with server-side rendering
- [LangChain4j](https://docs.langchain4j.dev/) - Java library for building AI applications
- [Qdrant](https://qdrant.tech/) - Vector similarity search engine
- [Anthropic Claude API](https://docs.anthropic.com/) - Claude Sonnet 4 model for chat functionality
