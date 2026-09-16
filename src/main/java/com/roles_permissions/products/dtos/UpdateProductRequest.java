package com.roles_permissions.products.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {
  private String name;
  private String description;

  @DecimalMin(value = "0.0", inclusive = true, message = "Price must be zero or greater" )
  private BigDecimal price;

  @Min(value = 0, message = "Quantity must be zero or greater" )
  private Integer quantity;
}
