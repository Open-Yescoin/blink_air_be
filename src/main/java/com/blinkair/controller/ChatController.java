package com.blinkair.controller;

import com.blinkair.dto.*;
import com.blinkair.security.UserPrincipal;
import com.blinkair.service.ChatService;
import com.blinkair.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final FriendService friendService;

    @GetMapping
    public ApiResponse<List<ChatVO>> getChats(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(chatService.getUserChats(principal.getUserId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<ChatVO> getChatDetail(@AuthenticationPrincipal UserPrincipal principal,
                                             @PathVariable Long id) {
        return ApiResponse.ok(chatService.getChatDetail(id, principal.getUserId()));
    }

    @GetMapping("/{id}/messages")
    public ApiResponse<List<MessageVO>> getMessages(@AuthenticationPrincipal UserPrincipal principal,
                                                    @PathVariable Long id,
                                                    @RequestParam(required = false) Long cursor,
                                                    @RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.ok(chatService.getMessages(id, principal.getUserId(), cursor, limit));
    }

    @PostMapping("/{id}/messages")
    public ApiResponse<MessageVO> sendMessage(@AuthenticationPrincipal UserPrincipal principal,
                                              @PathVariable Long id,
                                              @RequestBody SendMessageRequest request) {
        return ApiResponse.ok(chatService.sendMessage(id, principal.getUserId(), request.getContent()));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<Void> markRead(@AuthenticationPrincipal UserPrincipal principal,
                                      @PathVariable Long id) {
        chatService.markRead(id, principal.getUserId());
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/like")
    public ApiResponse<ChatVO> likeUser(@AuthenticationPrincipal UserPrincipal principal,
                                        @PathVariable Long id) {
        ChatVO chatVO = chatService.likeUser(id, principal.getUserId());
        friendService.checkAndCreateFriendship(id);
        return ApiResponse.ok(chatVO);
    }

    @PostMapping("/{id}/pass")
    public ApiResponse<Void> passChat(@AuthenticationPrincipal UserPrincipal principal,
                                      @PathVariable Long id) {
        chatService.passChat(id, principal.getUserId());
        return ApiResponse.ok();
    }
}
