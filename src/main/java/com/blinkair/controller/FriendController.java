package com.blinkair.controller;

import com.blinkair.dto.ApiResponse;
import com.blinkair.dto.FriendVO;
import com.blinkair.security.UserPrincipal;
import com.blinkair.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    public ApiResponse<List<FriendVO>> getFriends(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(friendService.getFriends(principal.getUserId()));
    }
}
