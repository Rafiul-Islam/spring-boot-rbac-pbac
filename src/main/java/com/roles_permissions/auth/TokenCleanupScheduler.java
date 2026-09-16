package com.roles_permissions.auth;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TokenCleanupScheduler {

  private static final Logger log = LoggerFactory.getLogger(TokenCleanupScheduler.class);
  private final ActiveTokenRepository activeTokenRepository;

  // fixedRate = 3600000 ms
  @Scheduled(fixedRate = 3600000)
  public void cleanupExpiredTokens() {
    activeTokenRepository.deleteExpiredTokens(LocalDateTime.now());
  }
}
