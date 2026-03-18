package com.blinkair.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomVO {
    private String id;
    private String name;
    private String icon;
    private int onlineCount;
}
