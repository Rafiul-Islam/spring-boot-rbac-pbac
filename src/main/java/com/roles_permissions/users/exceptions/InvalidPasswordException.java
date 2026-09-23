package com.roles_permissions.users.exceptions;

public class InvalidPasswordException extends RuntimeException {

  private static final String DEFAULT_MESSAGE = "Old password is incorrect.";

  public InvalidPasswordException() {
    this(DEFAULT_MESSAGE);
  }

  public InvalidPasswordException(String message) {
    super(message);
  }
}
