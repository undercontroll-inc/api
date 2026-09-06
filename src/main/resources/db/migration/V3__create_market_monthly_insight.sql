CREATE TABLE IF NOT EXISTS market_monthly_insight (
    id                     SERIAL PRIMARY KEY,
    bucket_key             VARCHAR(7)  NOT NULL,
    comparison_bucket_key  VARCHAR(7),
    status                 VARCHAR(20) NOT NULL,
    payload                TEXT,
    provider               VARCHAR(20),
    model                  VARCHAR(100),
    prompt_version         VARCHAR(32) NOT NULL,
    error_message          TEXT,
    generated_at           TIMESTAMP,
    created_at             TIMESTAMP   NOT NULL,
    updated_at             TIMESTAMP,
    CONSTRAINT uq_market_monthly_insight_bucket UNIQUE (bucket_key)
);

CREATE INDEX IF NOT EXISTS ix_market_monthly_insight_status
    ON market_monthly_insight (status);
