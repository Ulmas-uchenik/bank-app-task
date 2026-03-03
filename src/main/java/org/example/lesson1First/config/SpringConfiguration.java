package org.example.lesson1First.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.lesson1First.repository.TokenRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Log4j2
public class SpringConfiguration {
//    private final TokenRepository tokenRepository;

//    @Scheduled(fixedRate = 86400000) // Раз в сутки
//    @Transactional
//    public void cleanExpiredTokens() {
//        log.warn("TOKENS WAS CLEANED");
//        tokenRepository.deleteAllByExpiredTrueOrRevokedTrue();
//    }
}
