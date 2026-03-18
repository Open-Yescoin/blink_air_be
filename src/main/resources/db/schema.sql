-- BlinkAir new tables (ba_ prefix) in social_fantasy_bot database

CREATE TABLE IF NOT EXISTS ba_match_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    looking_for ENUM('MALE','FEMALE','ANY') NOT NULL DEFAULT 'ANY',
    status ENUM('WAITING','MATCHED','CANCELLED','EXPIRED') NOT NULL DEFAULT 'WAITING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    matched_at DATETIME NULL,
    INDEX idx_status_looking (status, looking_for),
    INDEX idx_user_status (user_id, status)
);

CREATE TABLE IF NOT EXISTS ba_chats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    status ENUM('ACTIVE','CLOSED') NOT NULL DEFAULT 'ACTIVE',
    user1_liked BOOLEAN NOT NULL DEFAULT FALSE,
    user2_liked BOOLEAN NOT NULL DEFAULT FALSE,
    message_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at DATETIME NULL,
    INDEX idx_user1 (user1_id, status),
    INDEX idx_user2 (user2_id, status)
);

CREATE TABLE IF NOT EXISTS ba_chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    type ENUM('TEXT','SYSTEM') NOT NULL DEFAULT 'TEXT',
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_chat_created (chat_id, created_at),
    FOREIGN KEY (chat_id) REFERENCES ba_chats(id)
);

CREATE TABLE IF NOT EXISTS ba_friendships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    chat_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_users (user1_id, user2_id),
    FOREIGN KEY (chat_id) REFERENCES ba_chats(id)
);
