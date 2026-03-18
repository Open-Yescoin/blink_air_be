package com.blinkair.service;

import com.blinkair.dto.FriendVO;
import com.blinkair.entity.Chat;
import com.blinkair.entity.Friendship;
import com.blinkair.entity.UserProfile;
import com.blinkair.exception.BizException;
import com.blinkair.repository.ChatRepository;
import com.blinkair.repository.FriendshipRepository;
import com.blinkair.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService {

    private final FriendshipRepository friendshipRepository;
    private final ChatRepository chatRepository;
    private final UserProfileRepository userProfileRepository;
    private final WebSocketNotificationService notificationService;

    public List<FriendVO> getFriends(Long userId) {
        List<Friendship> friendships = friendshipRepository.findByUserId(userId);
        return friendships.stream().map(f -> {
            Long friendId = f.getUser1Id().equals(userId) ? f.getUser2Id() : f.getUser1Id();
            UserProfile profile = userProfileRepository.findByUserId(friendId).orElse(null);
            return new FriendVO(
                    friendId,
                    profile != null ? profile.getName() : null,
                    null,
                    f.getChatId(),
                    f.getCreatedAt()
            );
        }).toList();
    }

    @Transactional
    public boolean checkAndCreateFriendship(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new BizException(404, "Chat not found"));

        if (!Boolean.TRUE.equals(chat.getUser1Liked()) || !Boolean.TRUE.equals(chat.getUser2Liked())) {
            return false;
        }

        // Check if already friends
        var existingFriendship = friendshipRepository.findByUserPair(chat.getUser1Id(), chat.getUser2Id());
        if (existingFriendship.isPresent()) {
            Friendship friendship = existingFriendship.get();
            if (!chatId.equals(friendship.getChatId())) {
                friendship.setChatId(chatId);
                friendshipRepository.save(friendship);
            }
            return true;
        }

        Long smallId = Math.min(chat.getUser1Id(), chat.getUser2Id());
        Long bigId = Math.max(chat.getUser1Id(), chat.getUser2Id());

        Friendship friendship = new Friendship();
        friendship.setUser1Id(smallId);
        friendship.setUser2Id(bigId);
        friendship.setChatId(chatId);
        friendshipRepository.save(friendship);

        log.info("New friendship: {} <-> {}, chatId={}", smallId, bigId, chatId);

        // Notify both users
        notificationService.notifyUser(chat.getUser1Id(), "/queue/friend", "NEW_FRIEND");
        notificationService.notifyUser(chat.getUser2Id(), "/queue/friend", "NEW_FRIEND");

        return true;
    }
}
