package com.blinkair.repository;

import com.blinkair.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE m.chatId = :chatId AND m.id < :cursor ORDER BY m.id DESC")
    List<ChatMessage> findByChatIdBeforeCursor(@Param("chatId") Long chatId, @Param("cursor") Long cursor, Pageable pageable);

    @Query("SELECT m FROM ChatMessage m WHERE m.chatId = :chatId ORDER BY m.id DESC")
    List<ChatMessage> findByChatIdLatest(@Param("chatId") Long chatId, Pageable pageable);
}
