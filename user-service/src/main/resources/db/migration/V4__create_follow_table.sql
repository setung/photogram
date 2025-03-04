CREATE TABLE IF NOT EXISTS  follow (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_id     BIGINT NOT NULL,
    target_id    BIGINT NOT NULL,
    status          VARCHAR(50) NOT NULL,
    created_at      DATETIME,
    updated_at      DATETIME,
    UNIQUE KEY uq_follower_following (requester_id, target_id),
    INDEX idx_follower_following (requester_id, target_id)
);
