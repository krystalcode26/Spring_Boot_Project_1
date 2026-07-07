package net.javaguides.ems.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;

class JsonAccessDeniedHandlerTest {

  private final JsonAccessDeniedHandler handler = new JsonAccessDeniedHandler();

  @Test
  void handle_returns403JsonBody() throws Exception {
    MockHttpServletResponse response = new MockHttpServletResponse();

    handler.handle(
        new MockHttpServletRequest(),
        response,
        new AccessDeniedException("denied"));

    assertThat(response.getStatus()).isEqualTo(403);
    assertThat(response.getContentAsString()).contains("\"status\":403");
    assertThat(response.getContentAsString()).contains("Access denied");
  }
}
