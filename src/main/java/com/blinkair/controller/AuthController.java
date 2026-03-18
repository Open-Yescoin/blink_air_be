package com.blinkair.controller;

import com.blinkair.dto.ApiResponse;
import com.blinkair.dto.LoginRequest;
import com.blinkair.dto.LoginResponse;
import com.blinkair.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/telegram/login")
    public ApiResponse<LoginResponse> telegramLogin(@RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.loginWithTelegram(request.getInitData()));
    }

    @PostMapping("/dev-login")
    public ApiResponse<LoginResponse> devLogin(@RequestParam Long telegramId) {
        return ApiResponse.ok(authService.devLogin(telegramId));
    }
}
