package com.roles_permissions.users.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRolesRequest {
  @NotNull(message = "At least one permission is required")
  @NotEmpty(message = "At least one role is required")
  private Set<String> roles;
}
