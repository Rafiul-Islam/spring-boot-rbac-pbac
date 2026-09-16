package com.roles_permissions.auth;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ActiveTokenRepository extends JpaRepository<ActiveToken, UUID> {
  boolean existsByTokenId(UUID token);

  @Transactional
  @Modifying
  @Query("DELETE FROM ActiveToken a WHERE a.expiryDate < :now")
  void deleteExpiredTokens(@Param("now") LocalDateTime now);
}