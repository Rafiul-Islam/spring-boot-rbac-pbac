package com.roles_permissions.users.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserPermissionsRequest {
  @NotEmpty(message = "At least one permission is required")
  private Set<String> permissions;
}
