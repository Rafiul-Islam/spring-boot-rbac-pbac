package com.roles_permissions.products.config;

import com.roles_permissions.common.SecurityRules;
import com.roles_permissions.users.enums.Permission;
import com.roles_permissions.users.enums.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class ProductSecurityRules implements SecurityRules {
  private static final String[] CAN_READ_ROLES = {Role.ADMIN.name(), Role.SUPER_ADMIN.name(), Role.MODERATOR.name(), Role.EDITOR.name()};
  private static final String[] CAN_CREATE_ROLES = {Role.ADMIN.name(), Role.SUPER_ADMIN.name()};
  private static final String[] CAN_UPDATE_ROLES = {Role.ADMIN.name(), Role.SUPER_ADMIN.name(), Role.MODERATOR.name()};
  private static final String[] CAN_DELETE_ROLES = {Role.ADMIN.name(), Role.SUPER_ADMIN.name()};

  @Override
  public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
    registry
      .requestMatchers(HttpMethod.GET, "/products/{id}").access(
        AuthorizationManagers.allOf(
          AuthorityAuthorizationManager.hasAnyRole(CAN_READ_ROLES),
          AuthorityAuthorizationManager.hasAuthority(Permission.PRODUCT_READ_SINGLE.name())
        )
      )
      .requestMatchers(HttpMethod.GET, "/products").access(
        AuthorizationManagers.allOf(
          AuthorityAuthorizationManager.hasAnyRole(CAN_READ_ROLES),
          AuthorityAuthorizationManager.hasAuthority(Permission.PRODUCT_READ_All.name())
        )
      )
      .requestMatchers(HttpMethod.POST, "/products").access(
        AuthorizationManagers.allOf(
          AuthorityAuthorizationManager.hasAnyRole(CAN_CREATE_ROLES),
          AuthorityAuthorizationManager.hasAuthority(Permission.PRODUCT_CREATE.name())
        )
      )
      .requestMatchers(HttpMethod.PUT, "/products/**").access(
        AuthorizationManagers.allOf(
          AuthorityAuthorizationManager.hasAnyRole(CAN_UPDATE_ROLES),
          AuthorityAuthorizationManager.hasAuthority(Permission.PRODUCT_UPDATE.name())
        )
      )
      .requestMatchers(HttpMethod.DELETE, "/products/**").access(
        AuthorizationManagers.allOf(
          AuthorityAuthorizationManager.hasAnyRole(CAN_DELETE_ROLES),
          AuthorityAuthorizationManager.hasAuthority(Permission.PRODUCT_DELETE.name())
        )
      );
  }
}