CREATE TABLE IF NOT EXISTS announcement (
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(255),
    content      VARCHAR(255),
    type         VARCHAR(50),
    image_key          VARCHAR(100),
    published_at TIMESTAMP,
    updated_at   TIMESTAMP
);
