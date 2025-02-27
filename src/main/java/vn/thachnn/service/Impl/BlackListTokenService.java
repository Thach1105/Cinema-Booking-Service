package vn.thachnn.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.thachnn.repository.BlackListTokenRepository;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "BLACKLIST-TOKEN-SERVICE")
public class BlackListTokenService {

    private final BlackListTokenRepository blackListTokenRepository;

    @Scheduled(fixedRate = 3600_000)
    public void cleanExpiredTokens() {
        log.info("Starting cleanup of expired tokens in blacklist");

        Date now = new Date();
        int deleteCount = blackListTokenRepository.deleteExpiredTokens(now);

        log.info("Cleanup completed! {} expired tokens removed.", deleteCount);
    }
}
