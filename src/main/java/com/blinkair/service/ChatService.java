package com.blinkair.service;

import com.blinkair.dto.ChatVO;
import com.blinkair.dto.MessageVO;
import com.blinkair.dto.UserVO;
import com.blinkair.entity.Chat;
import com.blinkair.entity.ChatMessage;
import com.blinkair.entity.User;
import com.blinkair.entity.UserProfile;
import com.blinkair.entity.enums.ChatStatus;
import com.blinkair.entity.enums.MessageType;
import com.blinkair.exception.BizException;
import com.blinkair.repository.ChatMessageRepository;
import com.blinkair.repository.ChatRepository;
import com.blinkair.repository.UserProfileRepository;
import com.blinkair.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final WebSocketNotificationService notificationService;

    public List<ChatVO> getUserChats(Long userId) {
        List<Chat> chats = chatRepository.findByUserId(userId);
        return chats.stream().map(c -> toChatVO(c, userId)).toList();
    }

    public ChatVO getChatDetail(Long chatId, Long userId) {
        Chat chat = getAndValidateChat(chatId, userId);
        return toChatVO(chat, userId);
    }

    public List<MessageVO> getMessages(Long chatId, Long userId, Long cursor, int limit) {
        getAndValidateChat(chatId, userId);
        List<ChatMessage> messages;
        if (cursor != null && cursor > 0) {
            messages = chatMessageRepository.findByChatIdBeforeCursor(chatId, cursor, PageRequest.of(0, limit));
        } else {
            messages = chatMessageRepository.findByChatIdLatest(chatId, PageRequest.of(0, limit));
        }
        return messages.stream().map(this::toMessageVO).toList();
    }

    @Transactional
    public MessageVO sendMessage(Long chatId, Long userId, String content) {
        Chat chat = getAndValidateChat(chatId, userId);
        if (chat.getStatus() != ChatStatus.ACTIVE) {
            throw new BizException(400, "Chat is closed");
        }

        ChatMessage message = new ChatMessage();
        message.setChatId(chatId);
        message.setSenderId(userId);
        message.setType(MessageType.TEXT);
        message.setContent(content);
        chatMessageRepository.save(message);

        chat.setMessageCount(chat.getMessageCount() + 1);
        chatRepository.save(chat);

        MessageVO vo = toMessageVO(message);

        // Push to the other user
        Long otherUserId = chat.getOtherUserId(userId);
        notificationService.notifyUser(otherUserId, "/queue/messages", vo);

        return vo;
    }

    @Transactional
    public void markRead(Long chatId, Long userId) {
        getAndValidateChat(chatId, userId);
        // Placeholder for read tracking — can add read_at field later
    }

    @Transactional
    public ChatVO likeUser(Long chatId, Long userId) {
        Chat chat = getAndValidateChat(chatId, userId);
        if (chat.getStatus() != ChatStatus.ACTIVE) {
            throw new BizException(400, "Chat is closed");
        }

        if (chat.isUser1(userId)) {
            chat.setUser1Liked(true);
        } else {
            chat.setUser2Liked(true);
        }
        chatRepository.save(chat);

        // Check if mutual like → friendship created by FriendService
        return toChatVO(chat, userId);
    }

    @Transactional
    public void passChat(Long chatId, Long userId) {
        Chat chat = getAndValidateChat(chatId, userId);
        chat.setStatus(ChatStatus.CLOSED);
        chat.setClosedAt(LocalDateTime.now());
        chatRepository.save(chat);
    }

    private Chat getAndValidateChat(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new BizException(404, "Chat not found"));
        if (!chat.isParticipant(userId)) {
            throw new BizException(403, "Not your chat");
        }
        return chat;
    }

    private ChatVO toChatVO(Chat chat, Long userId) {
        Long otherUserId = chat.getOtherUserId(userId);
        User otherUser = userRepository.findById(otherUserId).orElse(null);
        UserProfile otherProfile = userProfileRepository.findByUserId(otherUserId).orElse(null);

        boolean isUser1 = chat.isUser1(userId);
        return new ChatVO(
                chat.getId(),
                chat.getStatus().name(),
                otherUser != null ? UserVO.from(otherUser, otherProfile) : null,
                isUser1 ? chat.getUser1Liked() : chat.getUser2Liked(),
                isUser1 ? chat.getUser2Liked() : chat.getUser1Liked(),
                chat.getMessageCount(),
                chat.getCreatedAt()
        );
    }

    private MessageVO toMessageVO(ChatMessage msg) {
        return new MessageVO(
                msg.getId(), msg.getChatId(), msg.getSenderId(),
                msg.getType().name(), msg.getContent(), msg.getCreatedAt()
        );
    }
}
