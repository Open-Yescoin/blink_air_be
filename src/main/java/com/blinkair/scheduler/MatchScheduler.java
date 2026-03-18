package com.blinkair.scheduler;

import com.blinkair.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchScheduler {

    private final MatchService matchService;

    @Scheduled(fixedRate = 2000)
    public void processQueue() {
        try {
            matchService.processMatchQueue();
        } catch (Exception e) {
            log.error("Match scheduler error", e);
        }
    }
}
