CREATE TABLE IF NOT EXISTS public.users
(
    id             SERIAL PRIMARY KEY,
    username       VARCHAR(255) NOT NULL UNIQUE,
    email          VARCHAR(255),
    password_hash  VARCHAR(255) NOT NULL,
    role           VARCHAR(255),
    activation_key VARCHAR(255),
    apikey         VARCHAR(255),
    is_active      BOOLEAN               default FALSE,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     SERIAL REFERENCES users (id),
    updated_by     SERIAL REFERENCES users (id)
);