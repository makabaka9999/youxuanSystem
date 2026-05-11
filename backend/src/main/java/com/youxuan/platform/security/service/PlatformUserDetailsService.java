package com.youxuan.platform.security.service;

import com.youxuan.platform.security.domain.AuthPrincipal;
import com.youxuan.platform.security.domain.PrincipalType;
import com.youxuan.platform.security.repository.AuthAccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PlatformUserDetailsService implements UserDetailsService {

    private final AuthAccountRepository authAccountRepository;

    public PlatformUserDetailsService(AuthAccountRepository authAccountRepository) {
        this.authAccountRepository = authAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authAccountRepository.findUserByMobile(username)
                .map(AuthPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
    }

    public AuthPrincipal loadByPrincipal(PrincipalType principalType, Long principalId) {
        return authAccountRepository.findByPrincipal(principalType, principalId)
                .map(AuthPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("登录主体不存在"));
    }
}
