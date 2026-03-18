package com.blinkair.entity;

import com.blinkair.entity.enums.ChatStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ba_chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user1_id", nullable = false)
    private Long user1Id;

    @Column(name = "user2_id", nullable = false)
    private Long user2Id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChatStatus status = ChatStatus.ACTIVE;

    @Column(name = "user1_liked", nullable = false)
    private Boolean user1Liked = false;

    @Column(name = "user2_liked", nullable = false)
    private Boolean user2Liked = false;

    @Column(name = "message_count", nullable = false)
    private Integer messageCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    public boolean isParticipant(Long userId) {
        return user1Id.equals(userId) || user2Id.equals(userId);
    }

    public Long getOtherUserId(Long userId) {
        return user1Id.equals(userId) ? user2Id : user1Id;
    }

    public boolean isUser1(Long userId) {
        return user1Id.equals(userId);
    }
}
