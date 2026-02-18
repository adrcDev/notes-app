-- Indexes created for todos table
CREATE INDEX IF NOT EXISTS todos_title_tsvector_gin  ON todos USING GIN (title_tsvector);

CREATE INDEX IF NOT EXISTS todos_description_tsvector_gin  ON todos USING GIN (description_tsvector);

CREATE INDEX IF NOT EXISTS todos_content_text_tsvector_gin  ON todos USING GIN (content_text_tsvector);