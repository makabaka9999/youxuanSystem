package com.youxuan.platform.security.jwt;

import com.youxuan.platform.security.domain.AuthPrincipal;
import com.youxuan.platform.security.domain.PrincipalType;
import com.youxuan.platform.security.service.PlatformUserDetailsService;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final PlatformUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, PlatformUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveToken(request);
        if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = jwtTokenProvider.parseClaims(token);
                PrincipalType principalType = PrincipalType.valueOf(String.valueOf(claims.get("principalType")));
                Long principalId = Long.valueOf(String.valueOf(claims.get("principalId")));
                AuthPrincipal principal = userDetailsService.loadByPrincipal(principalType, principalId);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (InvalidJwtException ex) {
                throw new AuthenticationServiceException(ex.getMessage(), ex);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
}
