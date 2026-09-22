package com.roles_permissions.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roles_permissions.common.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException authException
  ) throws IOException {
    log.error("Authentication failed: {} - URI: {}", authException.getMessage(), request.getRequestURI());

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    ErrorDto errorDto = new ErrorDto("error", "Authentication failed: " + authException.getMessage());
    response.getWriter().write(objectMapper.writeValueAsString(errorDto));
  }
}
