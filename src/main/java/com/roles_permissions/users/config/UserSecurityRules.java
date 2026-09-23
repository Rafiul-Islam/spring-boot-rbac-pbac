package com.roles_permissions.users.config;

import com.roles_permissions.common.SecurityRules;
import com.roles_permissions.users.enums.Permission;
import com.roles_permissions.users.enums.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class UserSecurityRules implements SecurityRules {
  private static final String[] CAN_READ_USERS_ROLES = {Role.ADMIN.name(), Role.SUPER_ADMIN.name(), Role.MODERATOR.name(), Role.EDITOR.name()};
  private static final String[] CAN_READ_ROLE_ROLES = {Role.ADMIN.name(), Role.SUPER_ADMIN.name()};
  private static final String[] CAN_DELETE_USER_ROLES = {Role.ADMIN.name(), Role.SUPER_ADMIN.name()};
  private static final String[] CAN_UPDATE_ROLE_ROLES = {Role.ADMIN.name(), Role.SUPER_ADMIN.name()};
  private static final String[] CAN_READ_PERMISSION_ROLES = {Role.ADMIN.name(), Role.SUPER_ADMIN.name()};
  private static final String[] CAN_UPDATE_PERMISSION_ROLES = {Role.ADMIN.name(), Role.SUPER_ADMIN.name()};

  @Override
  public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
    registry
      .requestMatchers(HttpMethod.GET, "/users/*").access(
        AuthorizationManagers.anyOf(
          AuthorityAuthorizationManager.hasAuthority(Permission.USER_READ_SINGLE_OWN.name()),
          AuthorityAuthorizationManager.hasAuthority(Permission.USER_READ_SINGLE_OTHER.name())
        )
      )
      .requestMatchers(HttpMethod.GET, "/users").access(
        AuthorizationManagers.allOf(
          AuthorityAuthorizationManager.hasAnyRole(CAN_READ_USERS_ROLES),
          AuthorityAuthorizationManager.hasAuthority(Permission.USER_READ_All.name())
        )
      )
      .requestMatchers(HttpMethod.POST, "/users").permitAll()
      .requestMatchers(HttpMethod.GET, "/users/*/roles").hasAnyRole(CAN_READ_ROLE_ROLES)
      .requestMatchers(HttpMethod.GET, "/users/*/permissions").hasAnyRole(CAN_READ_PERMISSION_ROLES)
      .requestMatchers(HttpMethod.PUT, "/users/*/roles").hasAnyRole(CAN_UPDATE_ROLE_ROLES)
      .requestMatchers(HttpMethod.PUT, "/users/*/permissions").hasAnyRole(CAN_UPDATE_PERMISSION_ROLES)
      .requestMatchers(HttpMethod.DELETE, "/users/*").hasAnyRole(CAN_DELETE_USER_ROLES)
      .requestMatchers("/users/**").authenticated();
  }
}
