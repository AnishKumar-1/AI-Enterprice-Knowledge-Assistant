AI Enterprise Knowledge Assistant

Overview

An AI-powered enterprise knowledge assistant that allows employees to ask questions about internal company documents such as HR policies, guidelines, and operational procedures. The application uses Retrieval-Augmented Generation (RAG) with vector search to retrieve relevant document content and generate contextual responses using OpenAI.

/*  Features */

User registration and JWT authentication
Role-based access control
PDF document ingestion
OCR support for scanned PDFs
Vector embedding generation
Semantic search using pgvector
RAG-based question answering
Conversational memory (in-memory)
Document management
Flyway database migrations
Tech Stack
Java 17
Spring Boot 3.5
Spring AI
Spring Security
JWT Authentication
PostgreSQL
pgvector
OpenAI
Ollama
Flyway
Apache Tika
PDFBox
Tess4J
Lombok

/* Architecture  */

PDF Upload (Only Admin or super Admin)
     |
     v
Document Processing
(Tika/PDFBox/Tess4J)
     |
     v
Embedding Generation
(Ollama)
     |
     v
PostgreSQL + pgvector
     |
     v
RAG Retrieval
     |
     v
OpenAI
     |
     v
Response to User


/*  Future Enhancements  */
Redis-based persistent conversation memory
Docker support
Multi-document collections
Conversation history storage
Microservice architecture
AWS deployment
