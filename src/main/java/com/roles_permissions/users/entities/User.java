package com.roles_permissions.users.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "email")
  private String email;

  @Column(name = "password")
  private String password;

  @ManyToMany(cascade = {CascadeType.PERSIST})
  @JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  @Builder.Default
  private Set<Role> roles = new HashSet<>();

  @ManyToMany(cascade = {CascadeType.PERSIST})
  @JoinTable(
    name = "user_permissions",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "permission_id")
  )
  @Builder.Default
  private Set<Permission> permissions = new HashSet<>();

  @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
  @Builder.Default
  private List<Address> addresses = new ArrayList<>();

  public void addRole(Role role) {
    roles.add(role);
    role.getUsers().add(this);
  }

  public boolean hasRole(Role role) {
    return roles.contains(role);
  }

  public void addPermissions(Set<Permission> permissions) {
    permissions.forEach(p -> {
      this.permissions.add(p);
      p.getUsers().add(this);
    });
  }

  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", email='" + email + '\'' +
      ", password='" + password + '\'' +
      ", roles=" + roles +
      ", permissions=" + permissions +
      '}';
  }
}
