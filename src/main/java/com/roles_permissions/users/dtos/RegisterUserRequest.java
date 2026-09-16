package com.roles_permissions.users.dtos;

import com.roles_permissions.validations.LowerCase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
  @NotBlank(message = "Name is required" )
  @Size(min = 3, max = 255, message = "Name must be 3 to 255 characters long" )
  private String name;

  @NotBlank(message = "Email is required" )
  @Email(message = "Email must be valid" )
  @LowerCase(message = "Email must be in lowercase" )
  private String email;

  @NotBlank(message = "Password is required" )
  @Size(min = 6, max = 25, message = "Password must be 6 to 25 characters long" )
  private String password;
}
