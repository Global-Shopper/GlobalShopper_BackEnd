package com.sep490.gshop.config.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep490.gshop.common.enums.UserRole;
import com.sep490.gshop.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
//public class UserDetailsImpl implements UserDetails, OAuth2User {
public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private UUID id;

  private String name;

  private String username;

  private String email;

  private String avatar;

  @JsonIgnore
  private String password;

  private UserRole role = UserRole.CUSTOMER;

  private Map<String, Object> attributes;

  public UserDetailsImpl(UUID id, String name, String username, String email, String avatar, String password, UserRole role) {
    this.id = id;
    this.name = name;
    this.username = username;
    this.email = email;
    this.password = password;
    this.role = role;
    this.avatar = avatar;
  }

  public static UserDetailsImpl build(User user) {

    return new UserDetailsImpl(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getEmail(),
            user.getAvatar(),
        user.getPassword(),
        user.getRole());
  }

  public static UserDetailsImpl create(User user, Map<String, Object> attributes) {
    UserDetailsImpl userPrincipal = UserDetailsImpl.build(user);
    userPrincipal.setAttributes(attributes);
    userPrincipal.setName(user.getName());
    return userPrincipal;
  }


  //@Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_"+role.toString()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

//  @Override
//  public boolean equals(Object o) {
//    if (this == o)
//      return true;
//    if (o == null || getClass() != o.getClass())
//      return false;
//    UserDetailsImpl user = (UserDetailsImpl) o;
//    return Objects.equals(id, user.id);
//  }

  //@Override
  public String getName() {
    return name;
  }
}
