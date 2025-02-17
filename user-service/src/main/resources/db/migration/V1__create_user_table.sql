CREATE TABLE IF NOT EXISTS user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(255),
    name        VARCHAR(255),
    password    VARCHAR(255),
    created_at  DATETIME,
    updated_at  DATETIME,
    INDEX idx_user_email (email)
);