# Vintage Store

An AI-powered e-commerce application built with **Quarkus** that demonstrates sophisticated chat capabilities using **Retrieval Augmented Generation (RAG)** and real-time WebSocket communication.

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
docker compose -p vintagestore -f src/main/docker/postgresql.yml up -d

# Start Qdrant vector database (required for AI chat)
docker compose -p vintagestore -f src/main/docker/qdrant.yml up -d

# Start Redis for chat memory persistence  
docker compose -p vintagestore -f src/main/docker/redis.yml up -d

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

### Stop the application:

```bash
docker compose -p vintagestore down
```

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
- `org.agoncal.application.vintagestore.chat` - WebSocket chat implementation
- `org.agoncal.application.vintagestore.model` - Panache entities and data model
- `org.agoncal.application.vintagestore.rag` - Document ingestion and RAG components
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
