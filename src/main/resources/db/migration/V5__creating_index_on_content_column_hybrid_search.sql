CREATE INDEX vector_store_content_fts_idx ON vector_store
USING GIN(to_tsvector('english', content));