package com.roles_permissions.products.mappers;

import com.roles_permissions.products.dtos.CreateProductRequest;
import com.roles_permissions.products.dtos.ProductDto;
import com.roles_permissions.products.dtos.UpdateProductRequest;
import com.roles_permissions.products.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
  ProductDto toDto(Product product);
  List<ProductDto> toDtoList(List<Product> products);
  Product toEntity(CreateProductRequest request);
  void updateEntity(UpdateProductRequest request, @MappingTarget Product product);
}
