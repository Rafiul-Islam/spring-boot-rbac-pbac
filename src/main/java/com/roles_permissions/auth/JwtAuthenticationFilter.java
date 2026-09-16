package com.roles_permissions.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final ActiveTokenRepository activeTokenRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    var authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    var jwtToken = authHeader.replace("Bearer ", "");
    var jwt = jwtService.parseToken(jwtToken);
    if (jwt == null || jwt.isExpired()) {
      filterChain.doFilter(request, response);
      return;
    }

    boolean isTokenActive = activeTokenRepository.existsByTokenId(
      UUID.fromString(jwtService.parseToken(jwtToken).getJti())
    );
    if (!isTokenActive) {
      filterChain.doFilter(request, response);
      return;
    }

    Long userId = jwt.getUserId();
    var authorities = jwt.getUserRoles().stream()
      .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
      .collect(Collectors.toList());

    var authentication = new UsernamePasswordAuthenticationToken(
      userId,
      null,
      authorities
    );

    authentication.setDetails(
      new WebAuthenticationDetailsSource().buildDetails(request)
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }
}
