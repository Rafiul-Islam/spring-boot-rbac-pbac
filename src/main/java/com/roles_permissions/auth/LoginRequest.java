package com.roles_permissions.auth;

import com.roles_permissions.validations.LowerCase;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LoginRequest {
  @NotNull(message = "Email is required")
  @NotEmpty(message = "Email cannot be empty")
  @Email(message = "Email should be valid")
  @LowerCase(message = "Email should be in lowercase")
  private String email;

  @NotNull(message = "Password is required")
  @NotEmpty(message = "Password cannot be empty")
  @Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters")
  @Pattern.List({
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter"),
    @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter"),
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit"),
    @Pattern(regexp = ".*[^A-Za-z0-9].*", message = "Password must contain at least one special character")
  })
  private String password;
}
