package com.roles_permissions.products.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {
  @NotBlank(message = "Name is required" )
  @Size(min = 3, max = 255, message = "Name must be 3 to 255 characters long" )
  private String name;

  private String description;

  @NotNull(message = "Price is required" )
  @DecimalMin(value = "0.0", inclusive = true, message = "Price must be zero or greater" )
  private BigDecimal price;

  @NotNull(message = "Quantity is required" )
  @Min(value = 0, message = "Quantity must be zero or greater" )
  private Integer quantity;
}
