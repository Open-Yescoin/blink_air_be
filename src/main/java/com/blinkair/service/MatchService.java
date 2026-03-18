package com.blinkair.service;

import com.blinkair.dto.MatchStatusVO;
import com.blinkair.dto.UserVO;
import com.blinkair.entity.Chat;
import com.blinkair.entity.MatchQueue;
import com.blinkair.entity.User;
import com.blinkair.entity.UserProfile;
import com.blinkair.entity.enums.ChatStatus;
import com.blinkair.entity.enums.LookingFor;
import com.blinkair.entity.enums.MatchStatus;
import com.blinkair.exception.BizException;
import com.blinkair.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchService {

    private final MatchQueueRepository matchQueueRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final WebSocketNotificationService notificationService;

    @Transactional
    public MatchStatusVO joinQueue(Long userId, LookingFor lookingFor) {
        // Check if already in queue
        Optional<MatchQueue> existing = matchQueueRepository.findByUserIdAndStatus(userId, MatchStatus.WAITING);
        if (existing.isPresent()) {
            return toStatusVO(existing.get(), userId);
        }

        MatchQueue entry = new MatchQueue();
        entry.setUserId(userId);
        entry.setLookingFor(lookingFor);
        entry.setStatus(MatchStatus.WAITING);
        matchQueueRepository.save(entry);

        return toStatusVO(entry, userId);
    }

    public MatchStatusVO getMatchStatus(Long matchId, Long userId) {
        MatchQueue entry = matchQueueRepository.findById(matchId)
                .orElseThrow(() -> new BizException(404, "Match not found"));
        if (!entry.getUserId().equals(userId)) {
            throw new BizException(403, "Not your match");
        }
        return toStatusVO(entry, userId);
    }

    public MatchStatusVO getActiveMatch(Long userId) {
        Optional<MatchQueue> active = matchQueueRepository.findActiveByUserId(userId);
        if (active.isEmpty()) {
            return null;
        }
        return toStatusVO(active.get(), userId);
    }

    @Transactional
    public void cancelMatch(Long matchId, Long userId) {
        MatchQueue entry = matchQueueRepository.findById(matchId)
                .orElseThrow(() -> new BizException(404, "Match not found"));
        if (!entry.getUserId().equals(userId)) {
            throw new BizException(403, "Not your match");
        }
        if (entry.getStatus() != MatchStatus.WAITING) {
            throw new BizException(400, "Can only cancel waiting matches");
        }
        entry.setStatus(MatchStatus.CANCELLED);
        matchQueueRepository.save(entry);
    }

    @Transactional
    public void processMatchQueue() {
        List<MatchQueue> waiting = matchQueueRepository.findAllWaiting();
        if (waiting.size() < 2) return;

        for (int i = 0; i < waiting.size(); i++) {
            MatchQueue a = waiting.get(i);
            if (a.getStatus() != MatchStatus.WAITING) continue;

            UserProfile profileA = userProfileRepository.findByUserId(a.getUserId()).orElse(null);
            String genderA = profileA != null ? profileA.getGenderStr() : null;

            for (int j = i + 1; j < waiting.size(); j++) {
                MatchQueue b = waiting.get(j);
                if (b.getStatus() != MatchStatus.WAITING) continue;
                if (a.getUserId().equals(b.getUserId())) continue;

                UserProfile profileB = userProfileRepository.findByUserId(b.getUserId()).orElse(null);
                String genderB = profileB != null ? profileB.getGenderStr() : null;

                if (!isGenderMatch(a.getLookingFor(), genderB) || !isGenderMatch(b.getLookingFor(), genderA)) {
                    continue;
                }

                // Match found
                LocalDateTime now = LocalDateTime.now();
                a.setStatus(MatchStatus.MATCHED);
                a.setMatchedAt(now);
                b.setStatus(MatchStatus.MATCHED);
                b.setMatchedAt(now);

                Chat chat = chatRepository.findActiveByUserPair(a.getUserId(), b.getUserId())
                        .orElseGet(() -> {
                            Chat newChat = new Chat();
                            newChat.setUser1Id(a.getUserId());
                            newChat.setUser2Id(b.getUserId());
                            newChat.setStatus(ChatStatus.ACTIVE);
                            return chatRepository.save(newChat);
                        });

                matchQueueRepository.save(a);
                matchQueueRepository.save(b);

                // Notify both users via WebSocket
                notifyMatchResult(a.getUserId(), b.getUserId(), chat, a.getId(), b.getId());

                log.info("Matched users {} and {}, chatId={}", a.getUserId(), b.getUserId(), chat.getId());
                break;
            }
        }
    }

    private boolean isGenderMatch(LookingFor lookingFor, String gender) {
        if (lookingFor == LookingFor.ANY || gender == null) return true;
        return lookingFor.name().equals(gender);
    }

    private void notifyMatchResult(Long userId1, Long userId2, Chat chat, Long matchId1, Long matchId2) {
        User user1 = userRepository.findById(userId1).orElse(null);
        User user2 = userRepository.findById(userId2).orElse(null);
        UserProfile profile1 = userProfileRepository.findByUserId(userId1).orElse(null);
        UserProfile profile2 = userProfileRepository.findByUserId(userId2).orElse(null);

        MatchStatusVO vo1 = new MatchStatusVO(matchId1, "MATCHED", chat.getId(),
                user2 != null ? UserVO.from(user2, profile2) : null);
        MatchStatusVO vo2 = new MatchStatusVO(matchId2, "MATCHED", chat.getId(),
                user1 != null ? UserVO.from(user1, profile1) : null);

        notificationService.notifyUser(userId1, "/queue/match", vo1);
        notificationService.notifyUser(userId2, "/queue/match", vo2);
    }

    private MatchStatusVO toStatusVO(MatchQueue entry, Long userId) {
        MatchStatusVO vo = new MatchStatusVO();
        vo.setMatchId(entry.getId());
        vo.setStatus(entry.getStatus().name());

        if (entry.getStatus() == MatchStatus.MATCHED) {
            // Find the chat created for this match
            List<Chat> chats = chatRepository.findByUserId(userId);
            for (Chat chat : chats) {
                if (chat.getStatus() == ChatStatus.ACTIVE) {
                    Long otherUserId = chat.getOtherUserId(userId);
                    User otherUser = userRepository.findById(otherUserId).orElse(null);
                    UserProfile otherProfile = userProfileRepository.findByUserId(otherUserId).orElse(null);
                    vo.setChatId(chat.getId());
                    vo.setMatchedUser(otherUser != null ? UserVO.from(otherUser, otherProfile) : null);
                    break;
                }
            }
        }
        return vo;
    }
}
