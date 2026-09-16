package com.roles_permissions.products.services;

import com.roles_permissions.products.dtos.CreateProductRequest;
import com.roles_permissions.products.dtos.UpdateProductRequest;
import com.roles_permissions.products.entities.Product;
import com.roles_permissions.products.exceptions.ProductNotFoundException;
import com.roles_permissions.products.mappers.ProductMapper;
import com.roles_permissions.products.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServices {
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  public List<Product> findAll(String sortBy) {
    if (!Set.of("name", "price" ).contains(sortBy)) sortBy = "name";
    return productRepository.findAll(Sort.by(sortBy));
  }

  public Product findById(long productId) {
    return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found" ));
  }

  public Product save(CreateProductRequest request) {
    Product product = productMapper.toEntity(request);
    return productRepository.save(product);
  }

  public Product update(Long productId, UpdateProductRequest request) {
    Product savedProduct = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found" ));
    productMapper.updateEntity(request, savedProduct);
    return productRepository.save(savedProduct);
  }

  public void delete(Long productId) {
    Product savedProduct = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found" ));
    productRepository.delete(savedProduct);
  }
}
