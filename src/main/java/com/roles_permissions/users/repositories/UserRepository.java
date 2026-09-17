package com.roles_permissions.users.repositories;


import com.roles_permissions.users.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  @EntityGraph(attributePaths = {"roles", "permissions"})
  Optional<User> findByEmail(String email);

  @EntityGraph(attributePaths = {"roles", "permissions"})
  Optional<User> findById(@Param("id") Long id);
}
