package com.roles_permissions.products.config;

import com.roles_permissions.common.SecurityRules;
import com.roles_permissions.users.enums.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class ProductSecurityRules implements SecurityRules {
  private static final String[] CAN_READ = {Role.ADMIN.name(), Role.SUPER_ADMIN.name(), Role.MODERATOR.name(), Role.EDITOR.name()};
  private static final String[] CAN_CREATE = {Role.ADMIN.name(), Role.SUPER_ADMIN.name()};
  private static final String[] CAN_UPDATE = {Role.ADMIN.name(), Role.SUPER_ADMIN.name(), Role.MODERATOR.name()};
  private static final String[] CAN_DELETE = {Role.ADMIN.name(), Role.SUPER_ADMIN.name()};

  @Override
  public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
    registry
      .requestMatchers(HttpMethod.GET, "/products/**").hasAnyRole(CAN_READ)
      .requestMatchers(HttpMethod.POST, "/products").hasAnyRole(CAN_CREATE)
      .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyRole(CAN_UPDATE)
      .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole(CAN_DELETE);
  }
}
