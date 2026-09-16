package com.roles_permissions.users.services;

import com.roles_permissions.auth.JwtService;
import com.roles_permissions.users.dtos.ChangePasswordRequest;
import com.roles_permissions.users.dtos.RegisterUserRequest;
import com.roles_permissions.users.dtos.UpdateUserRequest;
import com.roles_permissions.users.dtos.UpdateUserRolesRequest;
import com.roles_permissions.users.entities.User;
import com.roles_permissions.users.enums.Permission;
import com.roles_permissions.users.enums.Role;
import com.roles_permissions.users.exceptions.UserNotFoundException;
import com.roles_permissions.users.mappers.UserMapper;
import com.roles_permissions.users.repositories.RoleRepository;
import com.roles_permissions.users.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServices {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final RoleRepository roleRepository;

  public List<User> findAll(String sortBy, String authHeader) {
    User currentUser = getCurrentUserWithAuthHeader(authHeader);
    requirePermission(currentUser, Permission.USER_READ_All);

    if (!Set.of("name", "email" ).contains(sortBy)) sortBy = "name";
    return userRepository.findAll(Sort.by(sortBy));
  }

  public User findById(long userId, String authHeader) {
    User currentUser = getCurrentUserWithAuthHeader(authHeader);
    Permission requiredPermission = currentUser.getId() == userId
      ? Permission.USER_READ_SINGLE_OWN
      : Permission.USER_READ_SINGLE_OTHER;
    requirePermission(currentUser, requiredPermission);

    return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found" ));
  }

  public Optional<User> getById(Long id) {
    return userRepository.findById(id);
  }

  public Optional<User> getByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Transactional
  public User save(RegisterUserRequest request) {
    userRepository.findByEmail(request.getEmail()).ifPresent((user) -> {
      throw new RuntimeException("Email is already registered" );
    });
    User user = userMapper.toEntity(request);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    var userRole = getRoleByName(Role.USER);
    var rolePermissions = userRole.getPermissions();
    user.addRole(userRole);
    user.addPermissions(rolePermissions);
    return userRepository.save(user);
  }

  public User update(Long userId, UpdateUserRequest request, String authHeader) {
    User currentUser = getCurrentUserWithAuthHeader(authHeader);
    requirePermission(currentUser, Permission.USER_UPDATE);

    User savedUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found" ));
    userMapper.updateEntity(request, savedUser);
    return userRepository.save(savedUser);
  }

  public User updateRoles(Long userId, UpdateUserRolesRequest request, String authHeader) {
    User currentUser = getCurrentUserWithAuthHeader(authHeader);
    requirePermission(currentUser, Permission.USER_UPDATE);

    User existingUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found" ));
    validateAccess(existingUser, currentUser);

    if (request.getRoles() == null || request.getRoles().isEmpty()) {
      throw new IllegalArgumentException("At least one role is required" );
    }

    Set<com.roles_permissions.users.entities.Role> roles = validateRoles(request);

    existingUser.addRoles(roles);
    return userRepository.save(existingUser);
  }

  public void delete(Long userId, String authHeader) {
    User currentUser = getCurrentUserWithAuthHeader(authHeader);
    requirePermission(currentUser, Permission.USER_DELETE);

    User savedUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found" ));
    userRepository.delete(savedUser);
  }

  public Boolean changePassword(Long userId, ChangePasswordRequest request, String authHeader) {
    User currentUser = getCurrentUserWithAuthHeader(authHeader);
    if (!currentUser.getId().equals(userId)) throw new AccessDeniedException("You are not allow to do this operation");

    User existingUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not found"));
    if (!existingUser.getPassword().equals(request.getOldPassword())) return false;
    existingUser.setPassword(request.getNewPassword());
    userRepository.save(existingUser);
    return true;
  }

  private com.roles_permissions.users.entities.Role getRoleByName(Role role) {
    return roleRepository.findByName(role.name()).orElseThrow(() -> new  RuntimeException("Role not found"));
  }

  private Set<com.roles_permissions.users.entities.Role> validateRoles(UpdateUserRolesRequest request) {
    Set<com.roles_permissions.users.entities.Role> roles = new HashSet<>();
    List<String> invalidRoles = new ArrayList<>();
    for (String roleName : request.getRoles()) {
      try {
        roles.add(getRoleByName(Role.valueOf(roleName)));
      } catch (IllegalArgumentException e) {
        invalidRoles.add(roleName);
      }
    }
    if (!invalidRoles.isEmpty()) {
      throw new IllegalArgumentException("Invalid role(s): " + String.join(", ", invalidRoles));
    }
    return roles;
  }

  private User getCurrentUserWithAuthHeader(String authHeader) {
    String jwtToken = authHeader.replace("Bearer ", "");
    Long currentUserId = jwtService.parseToken(jwtToken).getUserId();
    return userRepository.findById(currentUserId)
      .orElseThrow(() -> new UserNotFoundException("User not found" ));
  }

  private void requirePermission(User user, Permission permission) {
    if (!hasPermission(user, permission)) {
      throw new AccessDeniedException("You do not have permission to perform this action" );
    }
  }

  private boolean hasPermission(User user, Permission permission) {
    return user.getPermissions().stream()
      .anyMatch(p -> p.getName().equals(permission.name()));
  }

  private void validateAccess(User existingUser, User currentUser) {
    var superAdminRole = getRoleByName(Role.SUPER_ADMIN);
    if (existingUser.hasRole(superAdminRole) && !currentUser.hasRole(superAdminRole)) {
      throw new AccessDeniedException("Only a super admin can update another super admin's roles" );
    }
  }
}
