package com.blinkair.repository;

import com.blinkair.entity.Chat;
import com.blinkair.entity.enums.ChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE (c.user1Id = :userId OR c.user2Id = :userId) AND c.status = :status ORDER BY c.createdAt DESC")
    List<Chat> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ChatStatus status);

    @Query("SELECT c FROM Chat c WHERE (c.user1Id = :userId OR c.user2Id = :userId) ORDER BY c.createdAt DESC")
    List<Chat> findByUserId(@Param("userId") Long userId);
}
