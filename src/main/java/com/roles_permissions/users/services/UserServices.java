package com.roles_permissions.users.services;

import com.roles_permissions.auth.JwtService;
import com.roles_permissions.users.dtos.ChangePasswordRequest;
import com.roles_permissions.users.dtos.RegisterUserRequest;
import com.roles_permissions.users.dtos.UpdateUserRequest;
import com.roles_permissions.users.dtos.UpdateUserRolesRequest;
import com.roles_permissions.users.entities.Permission;
import com.roles_permissions.users.entities.User;
import com.roles_permissions.users.enums.Role;
import com.roles_permissions.users.exceptions.UserNotFoundException;
import com.roles_permissions.users.mappers.UserMapper;
import com.roles_permissions.users.repositories.PermissionRepository;
import com.roles_permissions.users.repositories.RoleRepository;
import com.roles_permissions.users.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
  private final PermissionRepository permissionRepository;

  public List<User> findAll(String sortBy) {
    if (!Set.of("name", "email" ).contains(sortBy)) sortBy = "name";
    return userRepository.findAll(Sort.by(sortBy));
  }

  public User findById(long userId) {
    return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found" ));
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

  public User updateRoles(Long userId, UpdateUserRolesRequest request, String authHeader) {
    String jwtToken = authHeader.replace("Bearer ", "");
    Set<Role> currentUserRoles = jwtService.parseToken(jwtToken).getUserRoles();
    User savedUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found" ));
    var superAdminRole = getRoleByName(Role.SUPER_ADMIN);
    if (savedUser.hasRole(superAdminRole) && !currentUserRoles.contains(Role.SUPER_ADMIN)) {
      throw new AccessDeniedException("Only a super admin can update another super admin's roles" );
    }

    if (request.getRoles() == null || request.getRoles().isEmpty()) {
      throw new IllegalArgumentException("At least one role is required" );
    }

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

    savedUser.addRoles(roles);
    return userRepository.save(savedUser);
  }

  public User update(Long userId, UpdateUserRequest request) {
    User savedUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found" ));
    userMapper.updateEntity(request, savedUser);
    return userRepository.save(savedUser);
  }

  public void delete(Long userId) {
    User savedUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found" ));
    userRepository.delete(savedUser);
  }

  public Boolean changePassword(Long userId, ChangePasswordRequest request) {
    Optional<User> optionalUser = userRepository.findById(userId);
    if (optionalUser.isEmpty()) {
      return false;
    }
    User user = optionalUser.get();
    if (!user.getPassword().equals(request.getOldPassword())) return false;
    user.setPassword(request.getNewPassword());
    userRepository.save(user);
    return true;
  }

  private com.roles_permissions.users.entities.Role getRoleByName(Role role) {
    return roleRepository.findByName(role.name()).orElseThrow(() -> new  RuntimeException("Role not found"));
  }

  public Optional<User> getByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Optional<User> getById(Long id) {
    return userRepository.findById(id);
  }
}
