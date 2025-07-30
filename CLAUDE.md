# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Development
```bash
./mvnw compile quarkus:dev          # Start development server with live reload
./mvnw test                         # Run unit tests
./mvnw verify                       # Run integration tests
./mvnw exec:java                    # Ingest documents for RAG system
```

### Build & Package
```bash
./mvnw package                      # Standard JAR build
./mvnw package -Dquarkus.package.jar.type=uber-jar  # Uber JAR
./mvnw package -Dnative             # Native executable
./mvnw package -Dnative -Dquarkus.native.container-build=true  # Container-based native build
```

### External Services Setup
```bash
# Start Qdrant vector database (required for AI chat)
docker compose -f src/main/docker/qdrant.yml up -d

# Start Redis for chat memory persistence
docker compose -f src/main/docker/redis.yml up -d

# Start PostgreSQL database (production database)
docker compose -f src/main/docker/postgresql.yml up -d

# Set Anthropic API key for chat functionality (Claude Sonnet 4)
export ANTHROPIC_API_KEY=your_api_key_here
```

## Architecture Overview

This is a **Quarkus 3.24.5** vintage store application that demonstrates AI-powered e-commerce with sophisticated chat capabilities.

### Core Technology Stack
- **Quarkus Renarde 3.1.1** - Web framework with type-safe Qute templating
- **LangChain4j 1.1.0** - AI integration with Anthropic Claude Sonnet 4 and RAG capabilities
- **Hibernate ORM with Panache** - Simplified data persistence
- **PostgreSQL 17.5** - Production database with comprehensive test data
- **Bootstrap 5.3.7** - Frontend UI framework with WebSocket chat integration

### AI/RAG System Architecture
The application features a sophisticated **Retrieval Augmented Generation (RAG)** system:
- **Qdrant vector database** stores document embeddings (384-dimensional)
- **AllMiniLM-L6-V2 model** generates embeddings for PDF documents in `/static/terms/`
- **Redis** persists chat memory with 20-message conversation windows
- **Function calling** enables real-time inventory queries during chat
- **VintageStoreChatBot** WebSocket endpoint at `/chat` provides real-time communication

### Data Model
Uses **single-table inheritance** with discriminator pattern:
```
Item (abstract base)
├── Book (extends Item)
│   ├── ManyToOne: Publisher, Category
│   ├── ManyToMany: Authors (via BOOK_AUTHOR table)
│   └── Attributes: ISBN, pages, publication date, language
└── CD (extends Item) 
    ├── ManyToOne: Label, Genre
    ├── ManyToMany: Musicians (via CD_MUSICIAN table)
    └── Attributes: number of discs
```

### Package Structure
- **`org.agoncal.application.vintagestore.web`** - Renarde controllers and static resources
- **`org.agoncal.application.vintagestore.api`** - REST API endpoints
- **`org.agoncal.application.vintagestore.model`** - Panache entities and data model
- **`org.agoncal.application.vintagestore.chat`** - WebSocket chat implementation
- **`org.agoncal.application.vintagestore.rag`** - Document ingestion and RAG components

### Template Architecture
Uses **Qute templating engine** with Renarde for type-safe templates:
- **`base.html`** - Main layout with integrated chat sidebar and navigation
- **Application templates** - Type-safe binding to controller methods
- **Bootstrap 5** responsive design with custom chat interface

### Database Configuration
- **Test data via `vintagestore-data.sql`** (300KB+ comprehensive dataset)
- **99 books** with categories, publishers, and author relationships
- **101 CDs** with genres, labels, and musician relationships  
- **Book-Author and CD-Musician junction tables** for many-to-many relationships
- **16+ authors and 100+ musicians** with detailed metadata

### API Endpoints
REST API follows predictable patterns:
- `/books`, `/books/categories`, `/books/publishers`, `/books/authors`
- `/cds`, `/cds/genres`, `/cds/labels`, `/cds/musicians`
- JSON-B serialization with OpenAPI documentation

### Development Workflow
1. Start external services (Qdrant, Redis, PostgreSQL) using docker-compose files
2. Set `ANTHROPIC_API_KEY` environment variable for Claude Sonnet 4
3. Run document ingestion: `./mvnw exec:java` (processes PDFs in `/static/terms/`)
4. Start development server: `./mvnw compile quarkus:dev`
5. Access application at http://localhost:8080
6. Chat functionality available via sidebar or `/chat` page

### Database Configuration Notes
- **PostgreSQL setup**: Uses custom initialization script at `src/main/docker/db-init/initialize-databases.sql`
- **Schema permissions**: Script grants necessary permissions to `vintagestore` user for table creation
- **Connection**: `jdbc:postgresql://localhost:5432/vintagestore_database?user=vintagestore&password=vintagestore`
- **DDL generation**: Configured to generate `create.sql` and `drop.sql` files for schema management

### Testing Strategy
- **Unit tests** for model validation and business logic
- **Integration tests** using RestAssured for API endpoints
- **Chat component tests** for AI functionality validation
- **Native compilation tests** for production readiness

### Key Development Notes
- **Live reload** enabled in dev mode for rapid development
- **Quarkus Dev UI** available at `/q/dev/` for debugging
- **Language enum** uses custom JPA converter for database mapping
- **Function tools pattern** allows AI to query inventory in real-time via `ItemsInStockTools` and `LegalDocumentTools`
- **Memory management** in chat system prevents token overflow (20-message window)
- **Document chunking** strategy optimized for legal document retrieval
- **Artist model** uses modern `java.time.LocalDate` and `Period` API for age calculation
- **WebSocket chat** at `/chat` endpoint provides real-time bidirectional communication
- **Anthropic Claude Sonnet 4** model (`claude-sonnet-4-20250514`) with 0.3 temperature for balanced creativity
