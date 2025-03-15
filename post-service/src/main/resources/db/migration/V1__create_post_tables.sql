CREATE TABLE post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    writer_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    contents VARCHAR(255) NOT NULL,
    created_at  DATETIME,
    updated_at  DATETIME,
    INDEX idx_wirter_id (writer_id)
);

CREATE TABLE tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    created_at  DATETIME,
    updated_at  DATETIME,
    INDEX idx_name (name)
);

CREATE TABLE post_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    INDEX idx_post_id (post_id),
    INDEX idx_tag_id (tag_id)
);

CREATE TABLE post_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    post_id BIGINT NOT NULL,
    created_at  DATETIME,
    updated_at  DATETIME,
    INDEX idx_post_id (post_id)
);
