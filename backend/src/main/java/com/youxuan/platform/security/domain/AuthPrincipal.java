package com.youxuan.platform.security.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthPrincipal implements UserDetails {

    private final AuthAccount account;

    public AuthPrincipal(AuthAccount account) {
        this.account = account;
    }

    public Long getPrincipalId() {
        return account.getPrincipalId();
    }

    public PrincipalType getPrincipalType() {
        return account.getPrincipalType();
    }

    public Long getUserId() {
        return account.getUserId();
    }

    public Long getMerchantId() {
        return account.getMerchantId();
    }

    public String getDisplayName() {
        return account.getDisplayName();
    }

    public Set<String> getRoles() {
        return account.getRoles();
    }

    public Set<String> getPermissions() {
        return account.getPermissions();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> authorities = new HashSet<>();
        account.getRoles().forEach(role -> authorities.add("ROLE_" + role));
        authorities.addAll(account.getPermissions());
        return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return account.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"DISABLED".equals(account.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ENABLED".equals(account.getStatus());
    }
}
