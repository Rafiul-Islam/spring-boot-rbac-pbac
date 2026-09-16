package com.roles_permissions.auth;


import com.roles_permissions.users.entities.User;
import com.roles_permissions.users.exceptions.UserNotFoundException;
import com.roles_permissions.users.services.UserServices;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserServices userServices;
  private final JwtConfig jwtConfig;
  private final ActiveTokenRepository activeTokenRepository;

  public String login (
    LoginRequest loginRequest,
    HttpServletResponse response
  ) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        loginRequest.getEmail(),
        loginRequest.getPassword()
      )
    );

    var user = userServices.getByEmail(loginRequest.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));

    var accessToken = jwtService.generateAccessToken(user).toString();
    var refreshToken = jwtService.generateRefreshToken(user).toString();

    var jwt = jwtService.parseToken(accessToken);

    activeTokenRepository.save(new ActiveToken(
      UUID.fromString(jwt.getJti()),
      jwt.getExpiration()
    ));

    var cookie = new Cookie("refresh_token", refreshToken);
    cookie.setHttpOnly(true);
    cookie.setPath("/auth/refresh");
    cookie.setMaxAge(Integer.parseInt(jwtConfig.getRefreshTokenExpirationInSeconds()));
    cookie.setSecure(true);
    response.addCookie(cookie);

    return accessToken;
  }

  public boolean validateAccessToken(String authHeader) {
    String jwtToken = authHeader.replace("Bearer ", "");
    boolean isAccessTokenExpired = jwtService.parseToken(jwtToken).isExpired();
    return !isAccessTokenExpired;
  }

  public Optional<User> getLoggedInUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    assert authentication != null;

    Long id = (Long) authentication.getPrincipal();
    assert id != null;

    return userServices.getById(id);
  }

  public String refreshAccessToken(String refreshToken) {
    var jwt = jwtService.parseToken(refreshToken);
    boolean result = jwt.isExpired();
    if (result) throw new RuntimeException("Invalid refresh token");
    var user = userServices.getById(jwt.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));
    return jwtService.generateAccessToken(user).toString();
  }

  public void logout(
    String authHeader,
    HttpServletResponse response
  ) {
    String existingAccessToken = authHeader.replace("Bearer ", "");
    activeTokenRepository.deleteById(UUID.fromString(jwtService.parseToken(existingAccessToken).getJti()));

    var cookie = new Cookie("refresh_token", "");
    cookie.setHttpOnly(true);
    cookie.setPath("/auth/refresh");
    cookie.setMaxAge(0);
    cookie.setSecure(true);
    response.addCookie(cookie);
  }

}
