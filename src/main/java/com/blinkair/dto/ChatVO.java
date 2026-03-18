package com.blinkair.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatVO {
    private Long id;
    private String status;
    private UserVO otherUser;
    private Boolean iLiked;
    private Boolean theyLiked;
    private Integer messageCount;
    private LocalDateTime createdAt;
}
