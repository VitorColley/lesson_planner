-- V1_init_curriculum_tables.sql
CREATE TABLE curriculum_documents (
    id BIGSERIAL PRIMARY KEY,
    subject VARCHAR(120) NOT NULL,
    cycle VARCHAR(120) NOT NULL,
    title VARCHAR(255) NOT NULL,
    source_type VARCHAR(40) NOT NULL,
    source_ref VARCHAR(500) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE curriculum_chunks (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL REFERENCES curriculum_documents(id) ON DELETE CASCADE,
    chunk_index INT NOT NULL,
    strand VARCHAR(120),
    element VARCHAR(120),
    learning_outcome_ref VARCHAR(120),
    content TEXT NOT NULL,
    metadata JSONB NOT NULL DEFAULT '{}' ::jsonb,
    source_page_start INT,
    source_page_end INT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT unique_chunk_per_document UNIQUE (document_id, chunk_index)
);

CREATE INDEX idx_chunks_document_id ON curriculum_chunks(document_id);
CREATE INDEX idx_chunks_strand ON curriculum_chunks(strand);
CREATE INDEX idx_chunks_element ON curriculum_chunks(element);
CREATE INDEX idx_chunks_learning_outcome_ref ON curriculum_chunks(learning_outcome_ref);

CREATE INDEX idx_chunks_content_tsv ON curriculum_chunks USING GIN (to_tsvector('english', content));

CREATE TABLE mapping_runs (
    id BIGSERIAL PRIMARY KEY,
    lesson_input TEXT NOT NULL,
    retrieved_chunk_ids JSONB NOT NULL DEFAULT '[]' ::jsonb,
    ai_response_json JSONB NOT NULL DEFAULT '{}' ::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_mapping_runs_created_at ON mapping_runs(created_at);
