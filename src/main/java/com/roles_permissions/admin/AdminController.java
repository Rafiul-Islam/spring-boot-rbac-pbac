package com.roles_permissions.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Admin", description = "All admin related endpoints")
@RequestMapping("/admin")
public class AdminController {
  @GetMapping("/greetings")
  @Operation(
    summary = "Admin greetings",
    description = "Returns a greeting message for admin users."
  )
  public String sayHello() {
    return "Hello Admin";
  }
}
