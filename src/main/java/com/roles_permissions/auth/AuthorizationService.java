package com.roles_permissions.auth;

import com.roles_permissions.users.entities.User;
import com.roles_permissions.users.enums.Permission;
import com.roles_permissions.users.exceptions.UserNotFoundException;
import com.roles_permissions.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
  private final UserRepository userRepository;
  private final JwtService jwtService;

  public User getCurrentUser(String authHeader) {
    String jwtToken = authHeader.replace("Bearer ", "");
    Long currentUserId = jwtService.parseToken(jwtToken).getUserId();
    return userRepository.findById(currentUserId)
      .orElseThrow(() -> new UserNotFoundException("User not found" ));
  }

  public void requirePermission(User user, Permission permission) {
    if (!hasPermission(user, permission)) {
      throw new AccessDeniedException("You do not have permission to perform this action" );
    }
  }

  public boolean hasPermission(User user, Permission permission) {
    return user.getPermissions().stream()
      .anyMatch(p -> p.getName().equals(permission.name()));
  }
}
