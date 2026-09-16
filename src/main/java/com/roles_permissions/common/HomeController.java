package com.roles_permissions.common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
  @RequestMapping("/")
  private String index(Model model) {
    model.addAttribute("name", "Rafi");
    return "index";
  }
}
