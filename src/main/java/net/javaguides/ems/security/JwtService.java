package net.javaguides.ems.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Profile("auth")
public class JwtService {

  private final JwtEncoder jwtEncoder;
  private final JwtProperties jwtProperties;

  public String generateToken(AuthAccount account) {
    return generateToken(account.email(), account.email(), account.name(), List.of(account.role()));
  }

  public String generateToken(String subject, String email, String name, List<String> roles) {
    Instant now = Instant.now();
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer("ems-backend")
        .issuedAt(now)
        .expiresAt(now.plusMillis(jwtProperties.getExpirationMs()))
        .subject(subject)
        .claim("email", email)
        .claim("name", name)
        .claim("roles", roles)
        .build();
    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
    return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
  }

  public static SecretKey secretKey(String secret) {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length < 32) {
      try {
        keyBytes = MessageDigest.getInstance("SHA-256").digest(keyBytes);
      } catch (NoSuchAlgorithmException ex) {
        throw new IllegalStateException("Unable to hash JWT secret", ex);
      }
    }
    return new javax.crypto.spec.SecretKeySpec(keyBytes, "HmacSHA256");
  }

  public static Collection<GrantedAuthority> rolesFromClaim(Object rolesClaim) {
    if (rolesClaim instanceof Collection<?> collection) {
      return collection.stream()
          .map(Object::toString)
          .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toSet());
    }
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }
}
