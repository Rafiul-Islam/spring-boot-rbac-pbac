package com.roles_permissions.products.exceptions;

public class ProductNotFoundException extends RuntimeException {

  private static final String DEFAULT_MESSAGE = "Product not found.";

  public ProductNotFoundException() {
    this(DEFAULT_MESSAGE);
  }

  public ProductNotFoundException(String message) {
    super(message);
  }
}
