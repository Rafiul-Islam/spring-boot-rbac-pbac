package com.roles_permissions.auth;

import com.roles_permissions.users.dtos.UserDto;
import com.roles_permissions.users.entities.User;
import com.roles_permissions.users.exceptions.UserNotFoundException;
import com.roles_permissions.users.mappers.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Tag(name = "Authentication", description = "All authentication related endpoints")
@RequestMapping("/auth")
public class AuthController {
  private final UserMapper userMapper;
  private final AuthService authService;

  @PostMapping("/login")
  @Operation(
    summary = "User login",
    description = "Authenticate user with credentials and return access token."
  )
  public ResponseEntity<LoginResponse> login(
    @RequestBody @Valid LoginRequest loginRequest,
    HttpServletResponse response
  ) {
    String accessToken = authService.login(loginRequest, response);
    return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(accessToken));
  }

  @PostMapping("/validate")
  @Operation(
    summary = "Validate token",
    description = "Check if the provided access token is valid."
  )
  public ResponseEntity<String> validateToken(
    @RequestHeader("Authorization") String authHeader
  ) {
    boolean isAccessTokenValid = authService.validateAccessToken(authHeader);
    if (!isAccessTokenValid) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token is invalid");
    return ResponseEntity.ok("Token is valid");
  }

  @GetMapping("/me")
  @Operation(
    summary = "Get current user",
    description = "Retrieve the currently authenticated user's details."
  )
  public ResponseEntity<UserDto> getCurrentUser() {
    User existingUser = authService.getLoggedInUser().orElseThrow(() -> new UserNotFoundException("User not found"));
    var userDto = userMapper.toDto(existingUser);
    return ResponseEntity.ok(userDto);
  }

  @Transactional
  @PostMapping("/refresh")
  @Operation(
    summary = "Refresh access token",
    description = "Generate a new access token using the refresh token from cookie."
  )
  public ResponseEntity<LoginResponse> refreshToken(
    @CookieValue(value = "refresh_token") String refreshToken
  ) {
    String newAccessToken = authService.refreshAccessToken(refreshToken);
    return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(newAccessToken));
  }

  @Transactional
  @PostMapping("/logout")
  @Operation(
    summary = "User logout",
    description = "Logout the current user and invalidate tokens."
  )
  public ResponseEntity<String> logout(
    @RequestHeader("Authorization") String authHeader,
    HttpServletResponse response
  ) {
    authService.logout(authHeader, response);
    return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
  }
}
