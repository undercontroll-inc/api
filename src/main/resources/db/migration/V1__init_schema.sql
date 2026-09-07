-- IF NOT EXISTS garante idempotência: seguro rodar em banco que já tem os dados
CREATE TABLE IF NOT EXISTS "user" (
    id                SERIAL PRIMARY KEY,
    name              VARCHAR(255),
    last_name         VARCHAR(255),
    email             VARCHAR(255),
    password          VARCHAR(255),
    address           VARCHAR(255),
    cpf               VARCHAR(255),
    cep               VARCHAR(8),
    phone             VARCHAR(255),
    already_recurrent BOOLEAN,
    in_first_login    BOOLEAN,
    avatar_url        VARCHAR(255),
    has_whats_app     BOOLEAN,
    created_at        TIMESTAMP,
    user_type         VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS "orders" (
    id               SERIAL PRIMARY KEY,
    user_id          INTEGER REFERENCES "user" (id) ON DELETE SET NULL,
    status           VARCHAR(50),
    total            DOUBLE PRECISION,
    discount         DOUBLE PRECISION,
    fabric_guarantee BOOLEAN NOT NULL DEFAULT FALSE,
    return_guarantee BOOLEAN NOT NULL DEFAULT FALSE,
    customer_description  VARCHAR(255),
    technical_description VARCHAR(255),
    nf               VARCHAR(255),
    date             TIMESTAMP,
    store            VARCHAR(255),
    updated_at       TIMESTAMP,
    received_at      DATE,
    completed_time   DATE,
    created_at       TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_item (
    id           SERIAL PRIMARY KEY,
    order_id     INTEGER REFERENCES "orders" (id) ON DELETE CASCADE,
    image_url    VARCHAR(255),
    volt         VARCHAR(255),
    series       VARCHAR(255),
    type         VARCHAR(255),
    brand        VARCHAR(255),
    model        VARCHAR(255),
    labor_value  DOUBLE PRECISION,
    completed_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS component (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255),
    description VARCHAR(255),
    brand       VARCHAR(255),
    price       DOUBLE PRECISION,
    supplier    VARCHAR(255),
    category    VARCHAR(255),
    quantity    BIGINT
);

CREATE TABLE IF NOT EXISTS demand (
    id           SERIAL PRIMARY KEY,
    quantity     BIGINT,
    component_id INTEGER REFERENCES component (id) ON DELETE CASCADE,
    order_id     INTEGER REFERENCES "orders" (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS refresh_token (
    id         BIGSERIAL PRIMARY KEY,
    token      VARCHAR(512)  NOT NULL UNIQUE,
    user_id    INTEGER       NOT NULL,
    user_email VARCHAR(255)  NOT NULL,
    user_role  VARCHAR(255)  NOT NULL,
    expires_at TIMESTAMPTZ   NOT NULL,
    revoked    BOOLEAN       NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS password_event (
    id         UUID PRIMARY KEY,
    type       VARCHAR(50),
    status     VARCHAR(50),
    value      VARCHAR(255),
    user_phone VARCHAR(255),
    user_agent VARCHAR(255),
    created_at TIMESTAMP
);
