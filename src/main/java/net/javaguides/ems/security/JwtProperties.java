package net.javaguides.ems.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

  private String secret = "change-me-use-env-JWT_SECRET-in-production";
  private long expirationMs = 3_600_000L;
}
