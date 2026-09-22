package com.roles_permissions.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorDto> handleBadCredentials(BadCredentialsException exception) {
    log.error("BadCredentialsException: {}", exception.getMessage());
    return new ResponseEntity<>(new ErrorDto("error", "Invalid email or password"), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorDto> handleAccessDenied(AccessDeniedException exception) {
    log.error("AccessDeniedException: {}", exception.getMessage());
    return new ResponseEntity<>(new ErrorDto("error", exception.getMessage()), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDto> handleValidationExceptions(MethodArgumentNotValidException exception) {
    Map<String, String> errors = new HashMap<>();

    exception.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    log.error("ValidationException: {}", errors);

    return ResponseEntity
      .badRequest()
      .body(new ErrorDto("error", errors.toString()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorDto> handleJsonParseException(HttpMessageNotReadableException exception) {
    log.error("HttpMessageNotReadableException: {}", exception.getMessage());

    return ResponseEntity
      .badRequest()
      .body(new ErrorDto("error", "Invalid request parameters"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDto> handleExceptions(Exception exception) {
    log.error("Exception: {}", exception.getMessage(), exception);

    return ResponseEntity
      .badRequest()
      .body(new ErrorDto("error", exception.getMessage()));
  }
}
