package com.roles_permissions.auth;

import com.roles_permissions.users.dtos.UserDto;
import com.roles_permissions.users.entities.User;
import com.roles_permissions.users.exceptions.UserNotFoundException;
import com.roles_permissions.users.mappers.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
  private final UserMapper userMapper;
  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
    @RequestBody @Valid LoginRequest loginRequest,
    HttpServletResponse response
  ) {
    String accessToken = authService.login(loginRequest, response);
    return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(accessToken));
  }

  @PostMapping("/validate")
  public ResponseEntity<String> validateToken(
    @RequestHeader("Authorization") String authHeader
  ) {
    boolean isAccessTokenValid = authService.validateAccessToken(authHeader);
    if (!isAccessTokenValid) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token is invalid");
    return ResponseEntity.ok("Token is valid");
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> getCurrentUser() {
    User existingUser = authService.getLoggedInUser().orElseThrow(() -> new UserNotFoundException("User not found"));
    var userDto = userMapper.toDto(existingUser);
    return ResponseEntity.ok(userDto);
  }

  @Transactional
  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refreshToken(
    @CookieValue(value = "refresh_token") String refreshToken
  ) {
    String newAccessToken = authService.refreshAccessToken(refreshToken);
    return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(newAccessToken));
  }

  @Transactional
  @PostMapping("/logout")
  public ResponseEntity<String> logout(
    @RequestHeader("Authorization") String authHeader,
    HttpServletResponse response
  ) {
    authService.logout(authHeader, response);
    return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
  }
}
