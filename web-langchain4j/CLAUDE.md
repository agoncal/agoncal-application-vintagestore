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
docker compose -f ../infrastructure/docker/qdrant.yml up -d

# Start Redis for chat memory persistence
docker compose -f ../infrastructure/docker/redis.yml up -d

# Start PostgreSQL database (production database)  
docker compose -f ../infrastructure/docker/postgresql.yml up -d

# Set Anthropic API key for chat functionality (Claude Sonnet 4)
export ANTHROPIC_API_KEY=your_api_key_here
```

### Logging
All public methods across controllers and resources have comprehensive entry logging using JBoss Logging:
```bash
# View application logs in development
./mvnw compile quarkus:dev
# Logs show method entries, user activities, authentication events, and API calls
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

User Management:
├── User (PanacheEntity)
│   ├── Attributes: login, password, firstName, lastName, email
│   ├── UserRole enum: USER, ADMIN
│   └── Table: VINTAGESTORE_USER
├── UserSession (@ApplicationScoped CDI bean)
│   └── Manages authentication state and current user
└── TemplateGlobals (static methods with @TemplateGlobal)
    └── Provides user(), isLoggedIn(), isAdmin() to all templates
```

### Package Structure
- **`org.agoncal.application.vintagestore.web`** - Renarde controllers and static resources
- **`org.agoncal.application.vintagestore.model`** - Panache entities and data model
- **`org.agoncal.application.vintagestore.chat`** - WebSocket chat implementation
- **`org.agoncal.application.vintagestore.rag`** - Document ingestion and RAG components

### Template Architecture
Uses **Qute templating engine** with Renarde for type-safe templates:
- **`base.html`** - Main layout with integrated chat sidebar, enhanced navigation, and user authentication UI
- **Application templates** - Type-safe binding to controller methods (signin.html, profile.html, users.html)
- **Bootstrap 5** responsive design with modern navbar (dark theme, rounded buttons, user dropdown)
- **Chat sidebar** - 700px wide with Markdown support via Marked.js, clear conversation functionality

### Database Configuration
- **Test data via `vintagestore-data.sql`** (300KB+ comprehensive dataset)
- **99 books** with categories, publishers, and author relationships
- **101 CDs** with genres, labels, and musician relationships  
- **Book-Author and CD-Musician junction tables** for many-to-many relationships
- **16+ authors and 100+ musicians** with detailed metadata including birth dates
- **20 users** with authentication data (16 USER role, 4 ADMIN role) for testing signin functionality

### Authentication System
Complete user authentication implemented with:
- **Sign-in/Sign-out** functionality with session management
- **User profile pages** with role-based features  
- **Admin user management** interface at `/view/users`
- **Session-scoped UserSession** CDI bean for state management
- **Template globals** for user state access across all templates
- **Role-based UI** (USER vs ADMIN with different menu options)

### Development Workflow
1. Start external services (Qdrant, Redis, PostgreSQL) using docker-compose files
2. Set `ANTHROPIC_API_KEY` environment variable for Claude Sonnet 4
3. Run document ingestion: `./mvnw exec:java` (processes PDFs in `/static/terms/`)
4. Start development server: `./mvnw compile quarkus:dev`
5. Access application at http://localhost:8080
6. Chat functionality available via sidebar or `/chat` page

### Database Configuration Notes
- **PostgreSQL setup**: Uses custom initialization script at `../infrastructure/docker/db-init/initialize-databases.sql`
- **Schema permissions**: Script grants necessary permissions to `vintagestore` user for table creation
- **Connection**: `jdbc:postgresql://localhost:5432/vintagestore_database?user=vintagestore&password=vintagestore`
- **DDL generation**: Configured to generate `create.sql` and `drop.sql` files for schema management

### Testing Strategy
- **Unit tests** for model validation and business logic
- **Chat component tests** for AI functionality validation
- **Native compilation tests** for production readiness

### Key Development Notes
- **Live reload** enabled in dev mode for rapid development
- **Quarkus Dev UI** available at `/q/dev/` for debugging
- **Language enum** uses custom JPA converter for database mapping
- **Function tools pattern** allows AI to query inventory in real-time via `ItemsInStockTools`, `LegalDocumentTools`, and `UserLoggedInTools`
- **Memory management** in chat system prevents token overflow (20-message window)
- **Document chunking** strategy optimized for legal document retrieval
- **Artist model** uses modern `java.time.LocalDate` and `Period` API for age calculation
- **WebSocket chat** at `/chat` endpoint provides real-time bidirectional communication with CLEAR_CONVERSATION command support
- **Anthropic Claude Sonnet 4** model (`claude-sonnet-4-20250514`) with 0.3 temperature for balanced creativity
- **Chat UI features**: Markdown rendering, 700px sidebar width, clear conversation button, enhanced typography
- **Comprehensive logging**: All public methods across controllers and APIs log method entries and important events
- **Authentication integration**: User state accessible in chat via UserLoggedInTools for personalized responses
