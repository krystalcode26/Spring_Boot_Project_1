package net.javaguides.ems.security;

import net.javaguides.ems.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.javaguides.ems.testutil.SecurityTestSupport.jwtService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginSuccessHandlerTest {

  @Mock
  private JwtProperties jwtProperties;

  @Mock
  private StudentRepository studentRepository;

  @Mock
  private AuthAccountService authAccountService;

  @Mock
  private PasswordEncoder passwordEncoder;

  private OAuth2LoginSuccessHandler handler;

  @BeforeEach
  void setUp() {
    handler = new OAuth2LoginSuccessHandler(
        jwtService(),
        jwtProperties,
        studentRepository,
        authAccountService,
        passwordEncoder);
    when(jwtProperties.getExpirationMs()).thenReturn(3_600_000L);
  }

  @Test
  void onAuthenticationSuccess_returnsBearerTokenJson() throws Exception {
    OAuth2User oauth2User = new DefaultOAuth2User(
        List.of(new SimpleGrantedAuthority("ROLE_USER")),
        Map.of("email", "user@example.com", "name", "Test User"),
        "email");
    var authentication = new PreAuthenticatedAuthenticationToken(oauth2User, null, oauth2User.getAuthorities());

    when(authAccountService.findByEmail("user@example.com"))
        .thenReturn(Optional.of(new AuthAccount(
            "user@example.com",
            "Test User",
            "ROLE_USER",
            "encoded-password")));

    MockHttpServletResponse response = new MockHttpServletResponse();
    handler.onAuthenticationSuccess(new MockHttpServletRequest(), response, authentication);

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getContentAsString()).contains("\"tokenType\":\"Bearer\"");
    assertThat(response.getContentAsString()).contains("\"accessToken\"");
    assertThat(response.getContentAsString()).contains("\"expiresIn\":3600");
  }
}
