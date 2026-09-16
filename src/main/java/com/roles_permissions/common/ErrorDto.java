package com.roles_permissions.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorDto {
  private String errorTitle;
  private String errorMessage;
}