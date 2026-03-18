package com.blinkair.controller;

import com.blinkair.dto.ApiResponse;
import com.blinkair.dto.MatchRequest;
import com.blinkair.dto.MatchStatusVO;
import com.blinkair.security.UserPrincipal;
import com.blinkair.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public ApiResponse<MatchStatusVO> startMatch(@AuthenticationPrincipal UserPrincipal principal,
                                                 @RequestBody MatchRequest request) {
        return ApiResponse.ok(matchService.joinQueue(principal.getUserId(), request.getLookingFor()));
    }

    @GetMapping("/{id}")
    public ApiResponse<MatchStatusVO> getMatchStatus(@AuthenticationPrincipal UserPrincipal principal,
                                                     @PathVariable Long id) {
        return ApiResponse.ok(matchService.getMatchStatus(id, principal.getUserId()));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancelMatch(@AuthenticationPrincipal UserPrincipal principal,
                                         @PathVariable Long id) {
        matchService.cancelMatch(id, principal.getUserId());
        return ApiResponse.ok();
    }

    @GetMapping("/active")
    public ApiResponse<MatchStatusVO> getActiveMatch(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(matchService.getActiveMatch(principal.getUserId()));
    }
}
