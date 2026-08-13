package com.dg.ticketonserver.global.security.userdetails;

import com.dg.ticketonserver.user.domain.Role;
import com.dg.ticketonserver.user.domain.User;
import jakarta.annotation.Nullable;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Role role;

    private CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole();
    }

    private CustomUserDetails(Long id, Role role) {
        this.id = id;
        this.username = null;
        this.password = null;
        this.role = role;
    }

    public static CustomUserDetails from(User user) {
        return new CustomUserDetails(user);
    }

    public static CustomUserDetails of(Long id, Role role) {
        return new CustomUserDetails(id, role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public @Nullable String getPassword() {
        return password;
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
