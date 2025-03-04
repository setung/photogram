CREATE TABLE IF NOT EXISTS  profile_image (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    url             VARCHAR(255) NOT NULL,
    file_name       VARCHAR(255) NOT NULL,
    created_at      DATETIME,
    updated_at      DATETIME,
    UNIQUE KEY uq_file_name (url)
);

ALTER TABLE user
    ADD COLUMN profile_image_id BIGINT NULL;