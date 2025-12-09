package com.infynno.javastartup.startup.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.infynno.javastartup.startup.modules.auth.service.AccessTokenBlacklistService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {
    private final AccessTokenBlacklistService blacklistService;

    @Scheduled(cron = "0 0 * * * ?") // Hourly
    public void cleanup() {
        blacklistService.cleanupExpired();
        // Future: Cleanup old refresh tokens
    }
}
