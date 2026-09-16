package com.roles_permissions.users.dtos;

import lombok.Data;

@Data
public class ChangePasswordRequest {
  private String oldPassword;
  private String newPassword;
}
