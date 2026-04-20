CREATE TABLE IF NOT EXISTS announcement (
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(255),
    content      VARCHAR(255),
    type         VARCHAR(50),
    published_at TIMESTAMP,
    updated_at   TIMESTAMP
);
