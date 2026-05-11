package com.youxuan.platform.security.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "youxuan.security")
public class SecurityProperties {

    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Password password = new Password();
    private BootstrapAdmin bootstrapAdmin = new BootstrapAdmin();

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Cors getCors() {
        return cors;
    }

    public void setCors(Cors cors) {
        this.cors = cors;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public BootstrapAdmin getBootstrapAdmin() {
        return bootstrapAdmin;
    }

    public void setBootstrapAdmin(BootstrapAdmin bootstrapAdmin) {
        this.bootstrapAdmin = bootstrapAdmin;
    }

    public static class Jwt {
        private String issuer;
        private String secret;
        private long accessTokenTtlMinutes = 120;

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getAccessTokenTtlMinutes() {
            return accessTokenTtlMinutes;
        }

        public void setAccessTokenTtlMinutes(long accessTokenTtlMinutes) {
            this.accessTokenTtlMinutes = accessTokenTtlMinutes;
        }
    }

    public static class Cors {
        private String allowedOrigins = "http://localhost:5173,http://127.0.0.1:5173";

        public List<String> allowedOriginList() {
            return Arrays.asList(allowedOrigins.split("\\s*,\\s*"));
        }

        public String getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }

    public static class Password {
        private int strength = 12;

        public int getStrength() {
            return strength;
        }

        public void setStrength(int strength) {
            this.strength = strength;
        }
    }

    public static class BootstrapAdmin {
        private boolean enabled = false;
        private String username = "admin";
        private String password = "";
        private String displayName = "平台管理员";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }
}
