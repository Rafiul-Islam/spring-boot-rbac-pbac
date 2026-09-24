package com.roles_permissions.users.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "permissions", schema = "roles_permissions")
public class Permission {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "name")
  private String name;

  @ManyToMany(mappedBy = "permissions")
  private Set<User> users = new HashSet<>();

  @ManyToMany(mappedBy = "permissions")
  private Set<Role> roles = new HashSet<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Permission permission)) return false;
    return id != null && id.equals(permission.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}