package net.javaguides.ems.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.javaguides.ems.dto.TokenResponse;
import net.javaguides.ems.entity.Student;
import net.javaguides.ems.repository.StudentRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "security.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtService jwtService;
  private final JwtProperties jwtProperties;
  private final StudentRepository studentRepository;
  private final AuthAccountService authAccountService;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.frontend-url:http://localhost:4200}")
  private String frontendUrl;

  @Override
  public void onAuthenticationSuccess(@NonNull HttpServletRequest request,
                                      @NonNull HttpServletResponse response,
                                      @NonNull Authentication authentication) throws IOException {
    if (!(authentication.getPrincipal() instanceof OAuth2User oauth2User)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 authentication required");
      return;
    }
    String email = OAuth2Attributes.getString(oauth2User, "email");
    String username = email != null ? email : oauth2User.getName();

    if (authAccountService.findByEmail(username).isEmpty()) {
      String fullName = OAuth2Attributes.getString(oauth2User, "name");
      String firstName = "Google";
      String lastName = "User";
      if (fullName != null && !fullName.isBlank()) {
        String[] parts = fullName.trim().split("\\s+", 2);
        firstName = parts[0];
        lastName = parts.length > 1 ? parts[1] : "User";
      }
      studentRepository.save(new Student(
          null,
          firstName,
          lastName,
          username,
          passwordEncoder.encode(UUID.randomUUID().toString()),
          "ROLE_USER"));
    }

    AuthAccount account = authAccountService.findByEmail(username)
        .orElseThrow(() -> new IllegalStateException("OAuth account could not be resolved"));

    TokenResponse body = new TokenResponse(
        jwtService.generateToken(account),
        "Bearer",
        jwtProperties.getExpirationMs() / 1000);

    String redirectUrl = frontendUrl + "/auth/callback?accessToken="
        + URLEncoder.encode(body.getAccessToken(), StandardCharsets.UTF_8)
        + "&tokenType=" + URLEncoder.encode(body.getTokenType(), StandardCharsets.UTF_8)
        + "&expiresIn=" + body.getExpiresIn();
    response.sendRedirect(redirectUrl);
  }
}
