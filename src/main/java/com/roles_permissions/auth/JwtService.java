package com.roles_permissions.auth;

import com.roles_permissions.users.entities.Role;
import com.roles_permissions.users.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JwtService {

  private final JwtConfig jwtConfig;

  public Jwt generateAccessToken(User user) {
    return generateToken(user, Long.parseLong(jwtConfig.getAccessTokenExpirationInSeconds()));
  }

  public Jwt generateRefreshToken(User user) {
    return generateToken(user, Long.parseLong(jwtConfig.getRefreshTokenExpirationInSeconds()));
  }

  private Jwt generateToken(User user, long TokenExpirationInSeconds) {
    String tokenId = UUID.randomUUID().toString();

    List<String> roles = user.getRoles().stream()
      .map(Role::getName)
      .collect(Collectors.toList());

    var claims = Jwts.claims()
      .setId(tokenId)
      .setSubject(String.valueOf(user.getId()))
      .add("name", String.valueOf(user.getName()))
      .add("email", user.getEmail())
      .add("roles", roles)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + 1000 * TokenExpirationInSeconds))
      .build();

    return new Jwt(claims, jwtConfig.getSecretKey());
  }

  private Claims getClaims(String token) {
    return Jwts.parser()
      .verifyWith(jwtConfig.getSecretKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  public Jwt parseToken(String token) {
    try {
      var claims = getClaims(token);
      return new Jwt(claims, jwtConfig.getSecretKey());
    } catch (Exception e) {
      return null;
    }
  }
}
