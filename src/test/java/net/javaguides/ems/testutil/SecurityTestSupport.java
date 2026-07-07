package net.javaguides.ems.testutil;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import net.javaguides.ems.security.JwtProperties;
import net.javaguides.ems.security.JwtService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import java.util.List;

public final class SecurityTestSupport {

  public static final String TEST_SECRET = "test-secret-key-for-unit-tests-only-must-be-long-enough";

  private SecurityTestSupport() {}

  public static JwtService jwtService() {
    JwtProperties properties = new JwtProperties();
    properties.setSecret(TEST_SECRET);
    properties.setExpirationMs(3_600_000L);
    return new JwtService(jwtEncoder(), properties);
  }

  public static JwtEncoder jwtEncoder() {
    SecretKey secretKey = JwtService.secretKey(TEST_SECRET);
    OctetSequenceKey jwk = new OctetSequenceKey.Builder(secretKey.getEncoded())
        .algorithm(JWSAlgorithm.HS256)
        .keyID("ems-test-jwt-key")
        .build();
    JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwkSource);
  }

  public static JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withSecretKey(JwtService.secretKey(TEST_SECRET)).build();
  }

  public static String bearerToken() {
    return jwtService().generateToken(
        "user@example.com",
        "user@example.com",
        "Test User",
        List.of("USER"));
  }
}
