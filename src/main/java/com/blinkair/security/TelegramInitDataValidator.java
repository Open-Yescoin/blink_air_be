package com.blinkair.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TelegramInitDataValidator {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TelegramUser validate(String initData) {
        try {
            Map<String, String> params = parseInitData(initData);
            String hash = params.remove("hash");

            if (hash == null) {
                throw new RuntimeException("Missing hash in initData");
            }

            String dataCheckString = params.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("\n"));

            byte[] secretKey = hmacSha256("WebAppData".getBytes(StandardCharsets.UTF_8),
                    botToken.getBytes(StandardCharsets.UTF_8));
            String computedHash = bytesToHex(hmacSha256(secretKey,
                    dataCheckString.getBytes(StandardCharsets.UTF_8)));

            if (!computedHash.equalsIgnoreCase(hash)) {
                log.warn("Invalid initData signature");
                throw new RuntimeException("Invalid initData signature");
            }

            String authDateStr = params.get("auth_date");
            if (authDateStr != null) {
                long authDate = Long.parseLong(authDateStr);
                if (Instant.now().getEpochSecond() - authDate > 300) {
                    throw new RuntimeException("initData expired");
                }
            }

            String userJson = params.get("user");
            if (userJson == null) {
                throw new RuntimeException("Missing user in initData");
            }

            return objectMapper.readValue(userJson, TelegramUser.class);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to validate initData", e);
            throw new RuntimeException("Failed to validate initData: " + e.getMessage());
        }
    }

    private Map<String, String> parseInitData(String initData) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = initData.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    private byte[] hmacSha256(byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
