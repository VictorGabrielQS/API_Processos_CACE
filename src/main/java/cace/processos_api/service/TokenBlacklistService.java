package cace.processos_api.service;


import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public void blacklistToken(String token, Duration duration) {
        Instant expiration = Instant.now().plus(duration);
        blacklist.put(token, expiration);
    }

    public boolean isBlacklisted(String token) {
        Instant expiration = blacklist.get(token);
        if (expiration == null) return false;

        // Remove se expirado
        if (Instant.now().isAfter(expiration)) {
            blacklist.remove(token);
            return false;
        }

        return true;
    }
}