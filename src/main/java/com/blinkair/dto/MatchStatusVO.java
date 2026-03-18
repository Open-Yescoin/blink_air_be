package com.blinkair.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchStatusVO {
    private Long matchId;
    private String status;
    private Long chatId;
    private UserVO matchedUser;
}
