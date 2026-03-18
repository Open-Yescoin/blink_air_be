package com.blinkair.entity;

import com.blinkair.entity.enums.LookingFor;
import com.blinkair.entity.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ba_match_queue")
public class MatchQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "looking_for", nullable = false)
    private LookingFor lookingFor = LookingFor.ANY;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MatchStatus status = MatchStatus.WAITING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "matched_at")
    private LocalDateTime matchedAt;
}
