package com.roles_permissions.auth;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "active_tokens")
public class ActiveToken {
  @Id
  @Column(name = "token")
  private UUID tokenId;

  @Column(name = "expiry_date", nullable = false)
  private LocalDateTime expiryDate;
}