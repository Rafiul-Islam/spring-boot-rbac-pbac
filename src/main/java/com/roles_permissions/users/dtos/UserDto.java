package com.roles_permissions.users.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.roles_permissions.users.entities.Permission;
import com.roles_permissions.users.entities.Role;
import lombok.*;

import java.util.Set;

@Data
public class UserDto {
  private Long id;
  private String name;
  private String email;

  @JsonIgnoreProperties({"permissions", "users"})
  private Set<Role> roles;

  @JsonIgnoreProperties({"roles", "users"})
  private Set<Permission> permissions;
}
