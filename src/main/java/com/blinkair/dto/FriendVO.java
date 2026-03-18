package com.blinkair.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendVO {
    private Long userId;
    private String name;
    private String media;
    private Long chatId;
    private LocalDateTime friendSince;
}
