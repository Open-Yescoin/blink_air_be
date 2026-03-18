package com.blinkair.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO {
    private Long id;
    private Long chatId;
    private Long senderId;
    private String type;
    private String content;
    private LocalDateTime createdAt;
}
