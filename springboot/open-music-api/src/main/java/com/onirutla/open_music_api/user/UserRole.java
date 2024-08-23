package com.onirutla.open_music_api.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public enum UserRole {
    USER(Set.of()),
    ADMIN(Set.of()),
    MANAGER(Set.of());

    @Getter
    private final Set<UserPermission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        return getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .toList();
    }
}
