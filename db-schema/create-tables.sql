CREATE TABLE IF NOT EXISTS users
(
    id SERIAL PRIMARY KEY,
    user_name TEXT UNIQUE NOT NULL,
    display_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    date_of_birth DATE NOT NULL,
    account_created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    password_hash TEXT NOT NULL,
    phone_no VARCHAR(16) UNIQUE NOT NULL,
    gender TEXT NOT NULL CHECK(gender IN ('male', 'female', 'other', 'rather not say'))
);


CREATE TABLE IF NOT EXISTS todos
(
    id SERIAL PRIMARY KEY,
    title TEXT,
    description TEXT,
    content_text TEXT,
    content_delta JSONB,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    due_date DATE,
    priority TEXT NOT NULL DEFAULT 'low' CHECK (priority IN ('low','medium','high')),
    status TEXT NOT NULL DEFAULT 'not completed' CHECK (status IN ('not completed','completed')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    title_tsvector tsvector NOT NULL GENERATED ALWAYS AS (to_tsvector('english',COALESCE(title, ''))) STORED,
    description_tsvector tsvector NOT NULL GENERATED ALWAYS AS (to_tsvector('english', COALESCE(description, ''))) STORED,
    content_text_tsvector tsvector NOT NULL GENERATED ALWAYS AS (to_tsvector('english', COALESCE(content_text, ''))) STORED
);


CREATE TABLE IF NOT EXISTS pending_signup_users
(
    id SERIAL PRIMARY KEY,
    user_name TEXT UNIQUE NOT NULL,
    display_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    phone_no VARCHAR(16) UNIQUE NOT NULL,
    date_of_birth DATE NOT NULL,
    password_hash TEXT NOT NULL,
    gender TEXT NOT NULL CHECK (gender IN ('male', 'female', 'other', 'rather not say'))
);


CREATE TABLE IF NOT EXISTS signup_otps
(
    id SERIAL PRIMARY KEY,
    otp VARCHAR(6) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL '5 minutes'),
    pending_signup_user_id INT NOT NULL REFERENCES pending_signup_users(id) ON DELETE CASCADE,
    type TEXT NOT NULL CHECK (type IN ('email', 'phone')),
    UNIQUE(pending_signup_user_id, type)
);


CREATE TABLE IF NOT EXISTS jwt_refresh_tokens
(
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    refresh_token_hash TEXT UNIQUE NOT NULL,
    issued_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    jti_claim_value_uuid UUID UNIQUE NOT NULL,
    status TEXT NOT NULL CHECK (status IN ('valid', 'invalidated'))
);


CREATE TABLE IF NOT EXISTS invalidated_jwt_refresh_token_extension_periods
(
    id SERIAL PRIMARY KEY,
    invalidated_jwt_refresh_token_id INT UNIQUE NOT NULL REFERENCES jwt_refresh_tokens(id) ON DELETE CASCADE,
    extension_period_ends_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL '5 minutes')
);


CREATE TABLE IF NOT EXISTS forgot_password_otps
(
    id SERIAL PRIMARY KEY,
    otp VARCHAR(6) NOT NULL,
    user_id integer NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type TEXT NOT NULL CHECK (type IN ('phone', 'email')),
    expires_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL '5 minutes'),
    UNIQUE(user_id, type)
);


CREATE TABLE IF NOT EXISTS password_reset_tokens
(
    id SERIAL PRIMARY KEY,
	token UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    user_id INT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at TIMESTAMPTZ NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL '20 minutes')
);