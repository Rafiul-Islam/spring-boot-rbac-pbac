package com.roles_permissions.products.controllers;

import com.roles_permissions.products.dtos.CreateProductRequest;
import com.roles_permissions.products.dtos.ProductDto;
import com.roles_permissions.products.dtos.UpdateProductRequest;
import com.roles_permissions.products.entities.Product;
import com.roles_permissions.products.mappers.ProductMapper;
import com.roles_permissions.products.services.ProductServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Products", description = "All product related endpoints")
@RequestMapping("/products")
public class ProductController {

  private final ProductServices productServices;
  private final ProductMapper productMapper;

  @GetMapping
  @Operation(
    summary = "Get all products",
    description = "Retrieve list of products with optional sorting."
  )
  private List<ProductDto> getProducts(
    @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy,
    @RequestHeader("Authorization") String authHeader
  ) {
    List<Product> products = productServices.findAll(sortBy, authHeader);
    return productMapper.toDtoList(products);
  }

  @GetMapping("/{productId}")
  @Operation(
    summary = "Get product by ID",
    description = "Fetch a single product by its unique ID."
  )
  private ResponseEntity<ProductDto> getProductById(
    @PathVariable(name = "productId") Long productId,
    @RequestHeader("Authorization") String authHeader
  ) {
    Product product = productServices.findById(productId, authHeader);
    ProductDto productDto = productMapper.toDto(product);
    return ResponseEntity.ok(productDto);
  }

  @PostMapping
  @Operation(
    summary = "Create new product",
    description = "Create a new product."
  )
  private ResponseEntity<ProductDto> createProduct(
    @Valid @RequestBody CreateProductRequest request,
    UriComponentsBuilder uriComponentsBuilder,
    @RequestHeader("Authorization") String authHeader
  ) {
    Product savedProduct = productServices.save(request, authHeader);
    ProductDto productDto = productMapper.toDto(savedProduct);

    URI uri = uriComponentsBuilder
      .path("/api/products/{productId}")
      .buildAndExpand(savedProduct.getId())
      .toUri();

    return ResponseEntity.created(uri).body(productDto);
  }

  @PutMapping("/{productId}")
  @Operation(
    summary = "Update product",
    description = "Update product details by ID."
  )
  private ResponseEntity<ProductDto> updateProduct(
    @PathVariable(name = "productId") Long productId,
    @Valid @RequestBody UpdateProductRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    Product updatedProduct = productServices.update(productId, request, authHeader);
    ProductDto productDto = productMapper.toDto(updatedProduct);
    return ResponseEntity.ok(productDto);
  }

  @DeleteMapping("/{productId}")
  @Operation(
    summary = "Delete product",
    description = "Remove a product from the system by ID."
  )
  private ResponseEntity<Void> deleteProduct(
    @PathVariable(name = "productId") Long productId,
    @RequestHeader("Authorization") String authHeader
  ) {
    productServices.delete(productId, authHeader);
    return ResponseEntity.noContent().build();
  }
}
