-- Indexes created for todos table
CREATE INDEX IF NOT EXISTS todos_user_id_btree on todos USING btree(user_id);

CREATE INDEX IF NOT EXISTS todos_title_tsvector_gin  ON todos USING GIN (title_tsvector);

CREATE INDEX IF NOT EXISTS todos_description_tsvector_gin  ON todos USING GIN (description_tsvector);

CREATE INDEX IF NOT EXISTS todos_content_text_tsvector_gin  ON todos USING GIN (content_text_tsvector);

-- Index created for signup_otps table
CREATE INDEX IF NOT EXISTS signup_otps_pending_signup_user_id_btree ON signup_otps USING 
btree(pending_signup_user_id);

-- Index created for forgot_password_otps table
CREATE INDEX IF NOT EXISTS forgot_password_otps_user_id_btree ON forgot_password_otps USING btree(user_id);

-- Index created for jwt_refresh_tokens table
CREATE INDEX IF NOT EXISTS jwt_refresh_tokens_user_id_btree ON jwt_refresh_tokens USING btree(user_id);