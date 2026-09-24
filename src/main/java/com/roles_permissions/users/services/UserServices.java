package com.roles_permissions.users.services;

import com.roles_permissions.auth.AuthorizationService;
import com.roles_permissions.users.dtos.ChangePasswordRequest;
import com.roles_permissions.users.dtos.RegisterUserRequest;
import com.roles_permissions.users.dtos.UpdateUserPermissionsRequest;
import com.roles_permissions.users.dtos.UpdateUserRequest;
import com.roles_permissions.users.dtos.UpdateUserRolesRequest;
import com.roles_permissions.users.entities.User;
import com.roles_permissions.users.entities.Permission;
import com.roles_permissions.users.entities.Role;
import com.roles_permissions.users.exceptions.InvalidPasswordException;
import com.roles_permissions.users.exceptions.UserNotFoundException;
import com.roles_permissions.users.mappers.UserMapper;
import com.roles_permissions.users.repositories.PermissionRepository;
import com.roles_permissions.users.repositories.RoleRepository;
import com.roles_permissions.users.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServices {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final AuthorizationService authorizationService;

  public List<User> findAll() {
    return userRepository.findAllWithRolesAndPermissions();
  }

  public User findById(long userId, String authHeader) {
    User currentUser = authorizationService.getCurrentUser(authHeader);
    com.roles_permissions.users.enums.Permission requiredPermission = currentUser.getId() == userId
      ? com.roles_permissions.users.enums.Permission.USER_READ_SINGLE_OWN
      : com.roles_permissions.users.enums.Permission.USER_READ_SINGLE_OTHER;
    authorizationService.requirePermission(currentUser, requiredPermission);

    return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
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
      throw new RuntimeException("Email is already registered");
    });
    User user = userMapper.toEntity(request);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    var userRole = getRoleByName(com.roles_permissions.users.enums.Role.USER);
    var rolePermissions = userRole.getPermissions();
    user.addRole(userRole);
    user.addPermissions(rolePermissions);
    return userRepository.save(user);
  }

  public User update(Long userId, UpdateUserRequest request, String authHeader) {
    User currentUser = authorizationService.getCurrentUser(authHeader);
    if (!currentUser.getId().equals(userId)) throw new AccessDeniedException("You are not allow to do this operation");
    User targetUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    userMapper.updateEntity(request, targetUser);
    return userRepository.save(targetUser);
  }

  @Transactional
  public User updateRoles(Long userId, UpdateUserRolesRequest request, String authHeader) {
    User currentUser = authorizationService.getCurrentUser(authHeader);

    User existingUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    validateAdminLevelAccess(existingUser, currentUser);

    Set<Role> newRoles = validateRoles(request);

    Set<Role> addedRoles = new HashSet<>(newRoles);
    addedRoles.removeAll(existingUser.getRoles());

    Set<Role> removedRoles = new HashSet<>(existingUser.getRoles());
    removedRoles.removeAll(newRoles);

    Set<Permission> permissionsToAdd = addedRoles.stream()
      .flatMap(role -> role.getPermissions().stream())
      .collect(Collectors.toSet());

    Set<Role> remainingRoles = new HashSet<>(existingUser.getRoles());
    remainingRoles.retainAll(newRoles);

    Set<Permission> remainingRolePermissions = remainingRoles.stream()
      .flatMap(role -> role.getPermissions().stream())
      .collect(Collectors.toSet());

    Set<Permission> permissionsToRemove = removedRoles.stream()
      .flatMap(role -> role.getPermissions().stream())
      .filter(p -> !remainingRolePermissions.contains(p))
      .collect(Collectors.toSet());

    existingUser.getRoles().retainAll(newRoles);
    existingUser.getRoles().addAll(newRoles);

    existingUser.getPermissions().removeAll(permissionsToRemove);
    existingUser.getPermissions().addAll(permissionsToAdd);

    return userRepository.save(existingUser);
  }

  @Transactional
  public User updatePermissions(Long userId, UpdateUserPermissionsRequest request, String authHeader) {
    User currentUser = authorizationService.getCurrentUser(authHeader);
    User existingUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    validateAdminLevelAccess(existingUser, currentUser);

    Set<Permission> permissions = validatePermissions(request);
    existingUser.getPermissions().retainAll(permissions);
    existingUser.getPermissions().addAll(permissions);

    return userRepository.save(existingUser);
  }

  public void delete(Long userId) {
    User savedUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    userRepository.delete(savedUser);
  }

  public void changePassword(Long userId, ChangePasswordRequest request, String authHeader) {
    User currentUser = authorizationService.getCurrentUser(authHeader);
    if (!currentUser.getId().equals(userId)) throw new AccessDeniedException("You are not allow to do this operation");

    User existingUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not found"));
    if (!passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())) {
      throw new InvalidPasswordException();
    }
    existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(existingUser);
  }

  private Role getRoleByName(com.roles_permissions.users.enums.Role role) {
    return roleRepository.findByName(role.name()).orElseThrow(() -> new RuntimeException("Role not found"));
  }

  private Permission getPermissionByName(com.roles_permissions.users.enums.Permission permission) {
    return permissionRepository.findByName(permission.name()).orElseThrow(() -> new RuntimeException("Permission not found"));
  }

  private Set<Permission> validatePermissions(UpdateUserPermissionsRequest request) {
    Set<Permission> permissions = new HashSet<>();
    List<String> invalidPermissions = new ArrayList<>();
    for (String permissionName : request.getPermissions()) {
      try {
        permissions.add(getPermissionByName(com.roles_permissions.users.enums.Permission.valueOf(permissionName)));
      } catch (IllegalArgumentException e) {
        invalidPermissions.add(permissionName);
      }
    }
    if (!invalidPermissions.isEmpty()) {
      throw new IllegalArgumentException("Invalid permission(s): " + String.join(", ", invalidPermissions));
    }
    return permissions;
  }

  private Set<Role> validateRoles(UpdateUserRolesRequest request) {
    Set<Role> roles = new HashSet<>();
    List<String> invalidRoles = new ArrayList<>();
    for (String roleName : request.getRoles()) {
      try {
        roles.add(getRoleByName(com.roles_permissions.users.enums.Role.valueOf(roleName)));
      } catch (IllegalArgumentException e) {
        invalidRoles.add(roleName);
      }
    }
    if (!invalidRoles.isEmpty()) {
      throw new IllegalArgumentException("Invalid role(s): " + String.join(", ", invalidRoles));
    }
    return roles;
  }

  private void validateAdminLevelAccess(User existingUser, User currentUser) {
    var superAdminRole = getRoleByName(com.roles_permissions.users.enums.Role.SUPER_ADMIN);
    var adminRole = getRoleByName(com.roles_permissions.users.enums.Role.ADMIN);
    if (existingUser.hasRole(superAdminRole) && !currentUser.hasRole(superAdminRole)) {
      throw new AccessDeniedException("Only a super admin can update another super admin's roles");
    }
    if (existingUser.hasRole(adminRole) && !currentUser.hasRole(superAdminRole)) {
      throw new AccessDeniedException("Only a super admin can update admin's roles");
    }
  }

  public Set<Role> getUserRoles(Long userId) {
    User existingUser = getById(userId).orElseThrow(() -> new UserNotFoundException("User Not Found"));
    return existingUser.getRoles();
  }

  public Set<Permission> getUserPermissions(Long userId) {
    User existingUser = getById(userId).orElseThrow(() -> new UserNotFoundException("User Not Found"));
    return existingUser.getPermissions();
  }
}
