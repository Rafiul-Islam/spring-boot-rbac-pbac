package com.roles_permissions.users.repositories;

import com.roles_permissions.users.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
  Optional<Permission> findByName(String name);
}
