package com.roles_permissions.auth;

import com.roles_permissions.users.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Data;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class Jwt {
  private final Claims claims;
  private final SecretKey key;

  public Jwt(Claims claims, SecretKey key) {
    this.claims = claims;
    this.key = key;
  }

  public boolean isExpired() {
    return claims.getExpiration().before(new java.util.Date());
  }

  public Long getUserId() {
    return Long.valueOf(claims.getSubject());
  }

  @SuppressWarnings("unchecked")
  public Set<Role> getUserRoles() {
    List<String> roleNames = claims.get("roles", List.class);
    if (roleNames == null) return Set.of();
    return roleNames.stream().map(Role::valueOf).collect(Collectors.toSet());
  }

  @SuppressWarnings("unchecked")
  public Set<String> getUserPermissions() {
    List<String> permissionNames = claims.get("permissions", List.class);
    if (permissionNames == null) return Set.of();
    return Set.copyOf(permissionNames);
  }

  public String getJti() {
    return claims.getId();
  }

  public LocalDateTime getExpiration() {
    return claims.getExpiration().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
  }

  public String toString() {
    return Jwts.builder()
      .setClaims(claims)
      .signWith(key)
      .compact();
  }
}
