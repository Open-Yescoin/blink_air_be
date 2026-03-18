package com.blinkair.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Data
@Entity
@Immutable
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tg_id", nullable = false, unique = true)
    private Long tgId;

    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "language")
    private String language;

    @Column(name = "status")
    private Short status;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "is_vip")
    private Boolean isVip;

    @Column(name = "is_visible")
    private Boolean isVisible;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
