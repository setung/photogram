CREATE TABLE comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    writer_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at  DATETIME,
    updated_at  DATETIME
);

CREATE INDEX idx_comments_post_id ON comment(post_id);