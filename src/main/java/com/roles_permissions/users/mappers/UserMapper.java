package com.roles_permissions.users.mappers;

import com.roles_permissions.users.dtos.*;
import com.roles_permissions.users.entities.Permission;
import com.roles_permissions.users.entities.Role;
import com.roles_permissions.users.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDto toDto(User user);
  List<UserDto> toDtoList(List<User> users);
  Set<RoleDto> toRoleDtoList(Set<Role> roles);
  Set<PermissionDto> toPermissionDtoList(Set<Permission> permissions);
  User toEntity(RegisterUserRequest request);
  void updateEntity(UpdateUserRequest request, @MappingTarget User user);
}
