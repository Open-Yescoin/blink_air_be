package com.blinkair.ws;

import com.blinkair.dto.MessageVO;
import com.blinkair.dto.SendMessageRequest;
import com.blinkair.security.UserPrincipal;
import com.blinkair.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;

    @MessageMapping("/chat/{chatId}/send")
    public void sendMessage(@DestinationVariable Long chatId,
                            @Payload SendMessageRequest request,
                            SimpMessageHeaderAccessor headerAccessor) {
        Authentication auth = (Authentication) headerAccessor.getUser();
        if (auth == null) {
            log.warn("Unauthenticated WS message attempt");
            return;
        }
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        MessageVO vo = chatService.sendMessage(chatId, principal.getUserId(), request.getContent());
        log.debug("WS message sent: chatId={}, sender={}, msgId={}", chatId, principal.getUserId(), vo.getId());
    }
}
