package com.blinkair.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyUser(Long userId, String destination, Object payload) {
        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(), destination, payload);
            log.debug("Sent WS notification to user {} at {}", userId, destination);
        } catch (Exception e) {
            log.warn("Failed to send WS notification to user {}: {}", userId, e.getMessage());
        }
    }
}
