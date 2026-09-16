package com.roles_permissions.validations;

import jakarta.validation.ConstraintValidator;

public class LowerCaseValidator implements ConstraintValidator<LowerCase, String> {
  @Override
  public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
    if (value == null) return true;
    return value.equals(value.toLowerCase());
  }
}
