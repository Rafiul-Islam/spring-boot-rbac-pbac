package com.roles_permissions.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
  @GetMapping("/greetings")
  public String sayHello() {
    return "Hello Admin";
  }
}
