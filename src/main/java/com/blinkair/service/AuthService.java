package com.blinkair.service;

import com.blinkair.dto.LoginResponse;
import com.blinkair.dto.UserVO;
import com.blinkair.entity.User;
import com.blinkair.entity.UserProfile;
import com.blinkair.exception.BizException;
import com.blinkair.repository.UserProfileRepository;
import com.blinkair.repository.UserRepository;
import com.blinkair.security.JwtTokenProvider;
import com.blinkair.security.TelegramInitDataValidator;
import com.blinkair.security.TelegramUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final TelegramInitDataValidator telegramValidator;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public LoginResponse loginWithTelegram(String initData) {
        TelegramUser tgUser = telegramValidator.validate(initData);
        return loginByTelegramId(tgUser.getId());
    }

    public LoginResponse devLogin(Long telegramId) {
        return loginByTelegramId(telegramId);
    }

    private LoginResponse loginByTelegramId(Long telegramId) {
        User user = userRepository.findByTgId(telegramId)
                .orElseThrow(() -> new BizException(404, "User not found. Please register first."));

        if (user.getDeletedAt() != null) {
            throw new BizException(403, "User is deleted");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), telegramId);

        UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
        UserVO userVO = UserVO.from(user, profile);

        log.info("User logged in: id={}, tgId={}", user.getId(), telegramId);
        return new LoginResponse(token, userVO);
    }
}
