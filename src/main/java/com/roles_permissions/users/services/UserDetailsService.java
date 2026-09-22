package com.roles_permissions.users.services;

import com.roles_permissions.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    com.roles_permissions.users.entities.User existingUser = userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    // 1. Map Role names (prefixed with ROLE_)
    Stream<SimpleGrantedAuthority> roleAuthorities = existingUser.getRoles().stream()
      .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()));

    // 2. Map Permissions tied directly to the Roles
    Stream<SimpleGrantedAuthority> rolePermissionAuthorities = existingUser.getRoles().stream()
      .flatMap(role -> role.getPermissions().stream())
      .map(permission -> new SimpleGrantedAuthority(permission.getName()));

    // 3. Map Override Permissions tied directly to the User
    Stream<SimpleGrantedAuthority> directPermissionAuthorities = existingUser.getPermissions().stream()
      .map(permission -> new SimpleGrantedAuthority(permission.getName()));

    // 4. Combine all three streams into a distinct set of GrantedAuthorities
    Set<GrantedAuthority> authorities = Stream.concat(
        roleAuthorities,
        Stream.concat(rolePermissionAuthorities, directPermissionAuthorities)
      )
      .collect(Collectors.toSet()); // .toSet() automatically eliminates duplicate strings

    return new User(
      existingUser.getEmail(),
      existingUser.getPassword(),
      authorities
    );
  }
}
