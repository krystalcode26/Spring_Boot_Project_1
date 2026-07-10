package net.javaguides.ems.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import net.javaguides.ems.security.JwtProperties;
import net.javaguides.ems.security.JwtService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
@Profile("auth")
public class JwtConfig {

  @Bean
  SecretKey jwtSecretKey(JwtProperties jwtProperties) {
    return JwtService.secretKey(jwtProperties.getSecret());
  }

  @Bean
  JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
    OctetSequenceKey jwk = new OctetSequenceKey.Builder(jwtSecretKey.getEncoded())
        .algorithm(JWSAlgorithm.HS256)
        .keyID("ems-jwt-key")
        .build();
    JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwkSource);
  }

  @Bean
  JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
    return NimbusJwtDecoder.withSecretKey(jwtSecretKey).build();
  }
}
