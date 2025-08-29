# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Development Commands
```bash
# Start development server (from web module)
cd web
./mvnw compile quarkus:dev

# Build the entire project
./mvnw clean package

# Run tests
./mvnw test                    # Unit tests for all modules
./mvnw verify                  # Integration tests for all modules
cd web && ./mvnw test          # Test specific module

# Document ingestion for RAG (from rag module)
cd rag
./mvnw exec:java
```

### Essential Setup Commands
```bash
# Required external services
docker compose -p vintagestore -f infrastructure/docker/postgresql.yml up -d
docker compose -p vintagestore -f infrastructure/docker/qdrant.yml up -d  
docker compose -p vintagestore -f infrastructure/docker/redis.yml up -d

# Required environment variable
export ANTHROPIC_API_KEY=your_api_key_here
export MISTRAL_AI_API_KEY=your_api_key_here
export COHERE_API_KEY=your_api_key_here
export OPENAI_API_KEY=your_api_key_here

# Stop all services
docker compose -p vintagestore down
```

## Project Architecture

This is a **multi-module Quarkus application** demonstrating AI-powered e-commerce with sophisticated RAG capabilities.

### Module Structure
- **`infrastructure/`** - Docker configurations for PostgreSQL, Qdrant, Redis
- **`mcp-currency/`** - Currency conversion MCP server component  
- **`rag/`** - Document ingestion system for PDF processing into vector embeddings
- **`web/`** - Main web application with AI chat functionality

### Core Technology Stack
- **Quarkus 3.25.4** with Renarde web framework for type-safe templating
- **LangChain4j 1.3.0** for AI integration with Anthropic Claude Sonnet 4
- **PostgreSQL 17.5** with comprehensive test data (99 books, 101 CDs, 20 users)
- **Qdrant vector database** for 1024-dimensional document embeddings
- **Redis** for persistent chat memory (20-message conversation windows)
- **Bootstrap 5.3.7** for responsive UI with WebSocket chat integration

### AI/RAG Architecture
The application features sophisticated **Retrieval Augmented Generation**:
- **Document processing pipeline**: PDFs → text extraction → embedding generation → vector storage
- **Real-time chat system**: WebSocket endpoint `/chat` with function calling capabilities
- **Memory management**: Redis-backed conversation persistence with token overflow protection
- **Function tools**: `ItemsInStockTools`, `LegalDocumentTools`, `UserLoggedInTools` for real-time queries

### Data Model
Uses **single-table inheritance** with discriminator pattern:
```
Item (abstract base class)
├── Book (with Publishers, Authors, Categories)
└── CD (with Labels, Musicians, Genres)

User Management System:
├── User entity with authentication
├── UserRole enum (USER, ADMIN)  
└── Role-based access control
```

### Key Development Patterns
- **Hibernate ORM with Panache** for simplified data persistence
- **Qute templating** with type-safe controller binding
- **CDI beans** for session management (`UserSession`, `TemplateGlobals`)
- **WebSocket communication** for real-time chat with CLEAR_CONVERSATION support
- **Function calling pattern** allows AI to query inventory and user state in real-time

### Database Configuration
- **Connection**: `jdbc:postgresql://localhost:5432/vintagestore_database`
- **Test data**: 300KB+ dataset loaded via `vintagestore-data.sql`
- **Schema management**: Auto-generated `create.sql` and `drop.sql` files
- **Junction tables**: Many-to-many relationships for Book-Author, CD-Musician

### Development Workflow
1. Start external services using docker compose files in `infrastructure/docker/`
2. Set `ANTHROPIC_API_KEY` environment variable  
3. Ingest documents: `cd rag && ./mvnw exec:java`
4. Start development server: `cd web && ./mvnw compile quarkus:dev`
5. Access at http://localhost:8080 (Dev UI: http://localhost:8080/q/dev/)

### Testing Strategy
- **Unit tests** in each module for model validation and business logic
- **Integration tests** for chat functionality and AI tool validation  
- **Native compilation support** for production deployment
