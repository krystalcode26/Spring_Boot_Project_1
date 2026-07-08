package net.javaguides.ems.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.seed")
public record AuthSeedProperties(
    String userEmail,
    String userCredential,
    String userRole,
    String adminEmail,
    String adminCredential,
    String adminRole) {}
