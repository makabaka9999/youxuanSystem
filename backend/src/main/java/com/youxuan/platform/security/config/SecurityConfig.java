package com.youxuan.platform.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youxuan.platform.common.api.ApiResponse;
import com.youxuan.platform.common.api.ErrorCode;
import com.youxuan.platform.common.web.RequestIdHolder;
import com.youxuan.platform.security.jwt.JwtAuthenticationFilter;
import com.youxuan.platform.security.jwt.InvalidJwtException;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final SecurityProperties securityProperties;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(SecurityProperties securityProperties,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          ObjectMapper objectMapper) {
        this.securityProperties = securityProperties;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/auth/**", "/api/v1/health", "/actuator/health", "/actuator/info").permitAll()
                .antMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(securityProperties.getPassword().getStrength());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(securityProperties.getCors().allowedOriginList());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                "X-Request-Id",
                "X-Idempotency-Key",
                "X-Timestamp",
                "X-Signature"));
        configuration.setExposedHeaders(Arrays.asList("X-Request-Id"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(1800L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            Throwable cause = authException.getCause();
            ErrorCode errorCode = cause instanceof InvalidJwtException ? ErrorCode.TOKEN_INVALID : ErrorCode.AUTH_REQUIRED;
            writeError(response, errorCode);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> writeError(response, ErrorCode.PERMISSION_DENIED);
    }

    private void writeError(javax.servlet.http.HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ApiResponse.failure(errorCode, RequestIdHolder.get()));
    }
}
