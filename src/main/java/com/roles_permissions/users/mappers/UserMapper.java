package com.roles_permissions.users.mappers;

import com.roles_permissions.users.dtos.RegisterUserRequest;
import com.roles_permissions.users.dtos.UpdateUserRequest;
import com.roles_permissions.users.dtos.UserDto;
import com.roles_permissions.users.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDto toDto(User user);
  List<UserDto> toDtoList(List<User> users);
  User toEntity(RegisterUserRequest request);
  void updateEntity(UpdateUserRequest request, @MappingTarget User user);
}
