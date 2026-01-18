package com.qburst.blog_application.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import com.qburst.blog_application.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

    private UserEntity userEntity;

    public UserPrincipal(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 1. Get the role from your entity (assuming userEntity.getRole() returns "ADMIN" or "USER")
        String roles = userEntity.getRoles();

        // 2. Spring Security hasRole("ADMIN") looks for "ROLE_ADMIN"
        // return Collections.singleton(new SimpleGrantedAuthority(roles));

        if (roles == null || roles.isBlank()) {
            return Collections.emptyList();
        }

        // Splitting "ROLE_USER,ROLE_ADMIN" into individual authorities
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
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
}
