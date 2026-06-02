CREATE TABLE documents (

                           id BIGSERIAL PRIMARY KEY,

                           file_name VARCHAR(255) NOT NULL,

                           category VARCHAR(255) NOT NULL,

                           uploaded_by VARCHAR(255) NOT NULL,

                           file_hash VARCHAR(255) NOT NULL UNIQUE,

                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE vector_store (

                              id UUID PRIMARY KEY,

                              content TEXT NOT NULL,

                              metadata JSONB,

                              embedding VECTOR(768) NOT NULL,

                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX vector_store_embedding_idx
    ON vector_store
    USING hnsw (embedding vector_cosine_ops);


CREATE INDEX vector_store_metadata_idx
    ON vector_store
    USING GIN(metadata);