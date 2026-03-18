package com.blinkair.controller;

import com.blinkair.dto.ApiResponse;
import com.blinkair.dto.UserVO;
import com.blinkair.entity.User;
import com.blinkair.entity.UserProfile;
import com.blinkair.exception.BizException;
import com.blinkair.repository.UserProfileRepository;
import com.blinkair.repository.UserRepository;
import com.blinkair.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @GetMapping
    public ApiResponse<UserVO> getMe(@AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new BizException(404, "User not found"));
        UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
        return ApiResponse.ok(UserVO.from(user, profile));
    }

    @PatchMapping
    public ApiResponse<UserVO> updateMe(@AuthenticationPrincipal UserPrincipal principal,
                                        @RequestBody Map<String, Object> updates) {
        // Read-only user/profile tables — return current data
        // In the future, if BlinkAir needs its own profile fields, add a ba_user_profiles table
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new BizException(404, "User not found"));
        UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
        return ApiResponse.ok(UserVO.from(user, profile));
    }

    @PatchMapping("/location")
    public ApiResponse<Void> updateLocation(@AuthenticationPrincipal UserPrincipal principal,
                                            @RequestBody Map<String, Object> body) {
        // Placeholder — location is in read-only user_profiles
        return ApiResponse.ok();
    }
}
