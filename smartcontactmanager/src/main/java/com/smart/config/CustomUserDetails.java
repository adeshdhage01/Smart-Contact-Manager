package com.smart.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.smart.entities.User;

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * It adapts your application's User entity to Spring Security.
 */
public class CustomUserDetails implements UserDetails {

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Provide user authorities (roles) to Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
    }

    // Return user's password
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // Return user's email as username
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // Whether account is non-expired
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Whether account is non-locked
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Whether credentials are non-expired
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Whether account is enabled
    @Override
    public boolean isEnabled() {
        return true;
    }
}
