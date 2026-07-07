package net.javaguides.ems.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.util.List;

import static net.javaguides.ems.testutil.SecurityTestSupport.TEST_SECRET;
import static net.javaguides.ems.testutil.SecurityTestSupport.jwtDecoder;
import static net.javaguides.ems.testutil.SecurityTestSupport.jwtService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

  private JwtService jwtService;
  private JwtDecoder jwtDecoder;

  @BeforeEach
  void setUp() {
    jwtService = jwtService();
    jwtDecoder = jwtDecoder();
  }

  @Test
  void generateToken_containsSubjectEmailAndRoles() {
    String token = jwtService.generateToken(
        "user@example.com",
        "user@example.com",
        "Test User",
        List.of("USER"));

    Jwt jwt = jwtDecoder.decode(token);

    assertThat(jwt.getSubject()).isEqualTo("user@example.com");
    assertThat(jwt.getClaimAsString("email")).isEqualTo("user@example.com");
    assertThat(jwt.getClaimAsString("name")).isEqualTo("Test User");
    List<String> roles = jwt.getClaimAsStringList("roles");
    assertThat(roles).contains("USER");
  }

  @Test
  void secretKey_hashesShortSecretsToValidLength() {
    assertThat(JwtService.secretKey("short").getEncoded().length).isGreaterThanOrEqualTo(32);
    assertThat(JwtService.secretKey(TEST_SECRET).getAlgorithm()).isEqualTo("HmacSHA256");
  }

  @Test
  void decode_rejectsTamperedToken() {
    String token = jwtService.generateToken(
        "user@example.com",
        "user@example.com",
        "Test User",
        List.of("USER"));

    assertThatThrownBy(() -> jwtDecoder.decode(token + "invalid"))
        .isInstanceOf(JwtException.class);
  }

  @Test
  void rolesFromClaim_addsRolePrefix() {
    assertThat(JwtService.rolesFromClaim(List.of("USER")))
        .extracting(Object::toString)
        .containsExactlyInAnyOrder("ROLE_USER");
  }
}
