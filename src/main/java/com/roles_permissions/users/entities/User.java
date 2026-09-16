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
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Set<Role> roles = new HashSet<>();

  @ManyToMany(cascade = {CascadeType.PERSIST})
  @JoinTable(
    name = "user_permissions",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "permission_id")
  )
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Set<Permission> permissions = new HashSet<>();

  @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
  @Builder.Default
  private List<Address> addresses = new ArrayList<>();

  public void addAddress(Address address) {
    addresses.add(address);
    address.setUser(this);
  }

  public void removeAddress(Address address) {
    addresses.remove(address);
    address.setUser(null);
  }

  public void addRole(Role role) {
    roles.add(role);
    role.getUsers().add(this);
  }

  public void addRoles(Set<Role> roles) {
    roles.forEach(r -> {
      roles.add(r);
      r.getUsers().add(this);
    });
  }

  public void removeRole(Role role) {
    roles.remove(role);
    role.getUsers().remove(this);
  }

  public boolean hasRole(Role role) {
    return roles.contains(role);
  }

  public void addPermission(Permission permission) {
    permissions.add(permission);
    permission.getUsers().add(this);
  }

  public void addPermissions(Set<Permission> permissions) {
    permissions.forEach(p -> {
      this.permissions.add(p);
      p.getUsers().add(this);
    });
  }

  public void removePermission(Permission permission) {
    permissions.remove(permission);
    permission.getUsers().remove(this);
  }

  public boolean hasPermission(Permission permission) {
    return permissions.contains(permission);
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
