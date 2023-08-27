CREATE TABLE IF NOT EXISTS public.users (
    id             SERIAL PRIMARY KEY,
    username       VARCHAR(64)   NOT NULL UNIQUE,
    email          VARCHAR(255),
    password_hash  VARCHAR(2048) NOT NULL,
    role           VARCHAR(32),
    activation_key VARCHAR(64),
    apikey         VARCHAR(64),
    is_active      BOOLEAN                DEFAULT FALSE,
    created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     SERIAL REFERENCES users (id),
    updated_by     SERIAL REFERENCES users (id)
);