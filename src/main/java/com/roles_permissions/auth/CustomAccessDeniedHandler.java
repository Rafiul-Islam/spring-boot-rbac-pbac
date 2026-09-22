package com.roles_permissions.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roles_permissions.common.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
  private static final Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void handle(
    HttpServletRequest request,
    HttpServletResponse response,
    AccessDeniedException accessDeniedException
  ) throws IOException {
    log.error("Access denied: {} - URI: {}", accessDeniedException.getStackTrace(), request.getRequestURI());

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    ErrorDto errorDto = new ErrorDto("error", "Access Denied: You do not have the required permissions.");
    response.getWriter().write(objectMapper.writeValueAsString(errorDto));
  }
}
