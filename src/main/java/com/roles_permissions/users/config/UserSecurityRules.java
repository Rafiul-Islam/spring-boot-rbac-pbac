package com.roles_permissions.users.config;

import com.roles_permissions.common.SecurityRules;
import com.roles_permissions.users.enums.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class UserSecurityRules implements SecurityRules {
  @Override
  public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
    registry.requestMatchers(HttpMethod.POST, "/users").permitAll()
      .requestMatchers(HttpMethod.PUT, "/users/*/roles").hasAnyRole(Role.ADMIN.name(), Role.SUPER_ADMIN.name())
      .requestMatchers(HttpMethod.PUT, "/users/*/permissions").hasAnyRole(Role.ADMIN.name(), Role.SUPER_ADMIN.name())
      .requestMatchers("/users/**").authenticated();
  }
}
