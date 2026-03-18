package com.blinkair.controller;

import com.blinkair.dto.ApiResponse;
import com.blinkair.dto.RoomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final StringRedisTemplate redisTemplate;

    @GetMapping
    public ApiResponse<List<RoomVO>> getRooms() {
        int globalOnline = getOnlineCount("global");
        List<RoomVO> rooms = List.of(
                new RoomVO("global", "Global Lounge", "🌍", globalOnline)
        );
        return ApiResponse.ok(rooms);
    }

    private int getOnlineCount(String roomId) {
        String val = redisTemplate.opsForValue().get("ba:room:" + roomId + ":online");
        return val != null ? Integer.parseInt(val) : 0;
    }
}
