package com.roles_permissions.users.controllers;

import com.roles_permissions.users.dtos.*;
import com.roles_permissions.users.entities.Permission;
import com.roles_permissions.users.entities.Role;
import com.roles_permissions.users.mappers.UserMapper;
import com.roles_permissions.users.services.UserServices;
import com.roles_permissions.users.entities.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@Tag(name = "Users", description = "All user related endpoints")
@RequestMapping("/users")
public class UserController {

  private final UserServices userServices;
  private final UserMapper userMapper;

  @GetMapping
  @Operation(
    summary = "Get all users",
    description = "Retrieve list of users with optional sorting."
  )
  private List<UserDto> getUsers() {
    List<User> users = userServices.findAll();
    return userMapper.toDtoList(users);
  }

  @GetMapping("/{userId}")
  @Operation(
    summary = "Get user by ID",
    description = "Fetch a single user by their unique ID."
  )
  private ResponseEntity<UserDto> getUserById(
    @PathVariable Long userId,
    @RequestHeader("Authorization") String authHeader
  ) {
    User user = userServices.findById(userId, authHeader);
    UserDto userDto = userMapper.toDto(user);
    return ResponseEntity.ok(userDto);
  }

  @GetMapping("/{userId}/roles")
  @Operation(
    summary = "Get user roles by ID",
    description = "Fetch a single user roles by their unique ID."
  )
  private ResponseEntity<Set<RoleDto>> getUserRolesById(
    @PathVariable Long userId
  ) {
    Set<Role> userRoles = userServices.getUserRoles(userId);
    Set<RoleDto> roles = userMapper.toRoleDtoList(userRoles);
    return ResponseEntity.ok(roles);
  }

  @GetMapping("/{userId}/permissions")
  @Operation(
    summary = "Get user permissions by ID",
    description = "Fetch a single user permissions by their unique ID."
  )
  private ResponseEntity<Set<PermissionDto>> getUserPermissionsById(
    @PathVariable Long userId
  ) {
    Set<Permission> userPermissions = userServices.getUserPermissions(userId);
    Set<PermissionDto> permissions = userMapper.toPermissionDtoList(userPermissions);
    return ResponseEntity.ok(permissions);
  }

  @PostMapping
  @Operation(
    summary = "Register new user",
    description = "Create a new user account."
  )
  private ResponseEntity<UserDto> createUser(
    @Valid @RequestBody RegisterUserRequest request,
    UriComponentsBuilder uriComponentsBuilder
  ) {
    User savedUser = userServices.save(request);
    UserDto userDto = userMapper.toDto(savedUser);

    URI uri = uriComponentsBuilder
      .path("/api/users/{userId}")
      .buildAndExpand(savedUser.getId())
      .toUri();

    return ResponseEntity.created(uri).body(userDto);
  }

  @PutMapping("/{userId}")
  @Operation(
    summary = "Update user",
    description = "Update user details by ID."
  )
  private ResponseEntity<UserDto> updateUser(
    @PathVariable Long userId,
    @RequestBody UpdateUserRequest request
  ) {
    User updatedUser = userServices.update(userId, request);
    UserDto userDto = userMapper.toDto(updatedUser);
    return ResponseEntity.ok(userDto);
  }

  @DeleteMapping("/{userId}")
  @Operation(
    summary = "Delete user",
    description = "Remove a user from the system by ID."
  )
  private ResponseEntity<Void> deleteUser(
    @PathVariable Long userId
  ) {
    userServices.delete(userId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{userId}/roles")
  @Operation(
    summary = "Update user roles",
    description = "Replace the set of roles assigned to a user."
  )
  private ResponseEntity<UserDto> updateUserRoles(
    @PathVariable Long userId,
    @Valid @RequestBody UpdateUserRolesRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    User updatedUser = userServices.updateRoles(userId, request, authHeader);
    UserDto userDto = userMapper.toDto(updatedUser);
    return ResponseEntity.ok(userDto);
  }

  @PutMapping("/{userId}/permissions")
  @Operation(
    summary = "Update user permissions",
    description = "Assign additional permissions to a user."
  )
  private ResponseEntity<UserDto> updateUserPermissions(
    @PathVariable Long userId,
    @Valid @RequestBody UpdateUserPermissionsRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    User updatedUser = userServices.updatePermissions(userId, request, authHeader);
    UserDto userDto = userMapper.toDto(updatedUser);
    return ResponseEntity.ok(userDto);
  }

  @PostMapping("/{userId}/change-password")
  @Operation(
    summary = "Change user password",
    description = "Update password for a user after verifying current credentials."
  )
  private ResponseEntity<Void> changePassword(
    @PathVariable Long userId,
    @RequestBody ChangePasswordRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    Boolean result = userServices.changePassword(userId, request, authHeader);
    if (result) return ResponseEntity.ok().build();
    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  }
}