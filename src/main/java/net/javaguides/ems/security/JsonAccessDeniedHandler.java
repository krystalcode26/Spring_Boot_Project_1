package net.javaguides.ems.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(@NonNull HttpServletRequest request,
                     @NonNull HttpServletResponse response,
                     @NonNull AccessDeniedException accessDeniedException) throws IOException {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(
        "{\"status\":403,\"message\":\"Access denied\",\"timestamp\":\""
            + LocalDateTime.now() + "\",\"errors\":null}");
  }
}
