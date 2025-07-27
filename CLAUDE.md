# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Quarkus-based Java application called "Vintage Store" that implements an e-commerce platform with AI-powered chat functionality. The application combines traditional JPA/Hibernate persistence with modern LangChain4j AI capabilities for document retrieval and chat assistance.

## Core Architecture

**Framework Stack:**
- **Quarkus 3.24.5** - Main application framework
- **LangChain4j 1.1.0** - AI/ML integration for chat and RAG
- **Hibernate ORM with Panache** - Data persistence layer
- **Quarkus Renarde** - Web templating framework
- **H2 Database** - In-memory database for development

**AI/ML Components:**
- **OpenAI GPT-4o** - Chat language model (requires OPENAI_API_KEY environment variable)
- **Qdrant** - Vector database for embeddings (runs on localhost:6334)
- **Redis** - Chat memory storage (runs on localhost:6379)
- **AllMiniLmL6V2** - Local embedding model for document processing

## Key Commands

**Development:**
```bash
./mvnw compile quarkus:dev          # Start development mode with live reload
```

**Building:**
```bash
./mvnw package                      # Standard JAR build
./mvnw package -Dquarkus.package.jar.type=uber-jar  # Uber JAR build
```

**Testing:**
```bash
./mvnw test                         # Run unit tests
./mvnw verify                       # Run integration tests
```

**Document Ingestion:**
```bash
./mvnw exec:java                    # Ingest PDF documents into vector store
```

**Native Build:**
```bash
./mvnw package -Dnative             # Build native executable
./mvnw package -Dnative -Dquarkus.native.container-build=true  # Container-based native build
```

## Application Structure

**Core Packages:**
- `org.agoncal.application.vintagestore.web` - Web controllers and templating
- `org.agoncal.application.vintagestore.api` - REST API endpoints
- `org.agoncal.application.vintagestore.model` - JPA entities (Book, CD, Artist, etc.)
- `org.agoncal.application.vintagestore.chat` - AI chat bot and assistant implementation
- `org.agoncal.application.vintagestore.rag` - Document ingestion and RAG processing

**Key Files:**
- `Application.java` - Main web controller with Qute templates
- `VintageStoreChatBot.java` - WebSocket-based chat interface
- `VintageStoreChatAssistant.java` - AI assistant interface
- `DocumentIngestor.java` - PDF document processing for RAG
- `ItemsInStockTools.java` / `LegalDocumentTools.java` - Function calling tools

## Development Requirements

**External Services:**
- Qdrant vector database must be running on localhost:6334
- Redis server must be running on localhost:6379 for chat memory
- Set OPENAI_API_KEY environment variable for AI functionality

**Docker Compose Files:**
- `src/main/docker/qdrant.yml` - Qdrant configuration
- `src/main/docker/redis.yml` - Redis configuration

## AI Chat Features

The application includes a sophisticated chat system with:
- **RAG (Retrieval Augmented Generation)** - Uses embedded PDF documents from `src/main/resources/terms/`
- **Function Calling** - Tools for checking inventory and legal documents
- **Persistent Memory** - Redis-backed conversation history
- **WebSocket Interface** - Real-time chat at `/chat` endpoint

## Database Schema

The application uses JPA entities with Panache for simplified data access:
- **Item** hierarchy: Book and CD as main product types
- **Related entities**: Author, Artist, Musician, Publisher, Label, Genre, Category
- **Enum support**: Language with custom JPA converter

## Templates and Static Resources

- **Qute Templates**: Located in `src/main/resources/templates/`
- **Static Assets**: Bootstrap 5.3.7 and Bootstrap Icons via mvnpm
- **Styling**: Custom CSS in `src/main/resources/static/css/`

## Testing

Tests are organized by component:
- API tests use RestAssured
- Model tests verify entity behavior
- Chat component tests validate AI functionality
- Integration tests available with `-Dnative` profile
