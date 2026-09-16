package com.roles_permissions.users.exceptions;

public class UserNotFoundException extends RuntimeException {

  private static final String DEFAULT_MESSAGE = "User not found.";

  public UserNotFoundException() {
    this(DEFAULT_MESSAGE);
  }

  public UserNotFoundException(String message) {
    super(message);
  }
}
