package net.javaguides.ems.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@Profile("auth")
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(@NonNull HttpServletRequest request,
                       @NonNull HttpServletResponse response,
                       @NonNull AuthenticationException authException) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(
        "{\"status\":401,\"message\":\"Authentication required. Sign in via /oauth2/authorization/google and use the Bearer token.\",\"timestamp\":\""
            + DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
                java.time.OffsetDateTime.now(ZoneOffset.UTC)) + "\",\"errors\":null}");
  }
}
