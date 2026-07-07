package net.javaguides.ems.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class JsonAuthenticationEntryPointTest {

  private final JsonAuthenticationEntryPoint entryPoint = new JsonAuthenticationEntryPoint();

  @Test
  void commence_returns401JsonBody() throws Exception {
    MockHttpServletResponse response = new MockHttpServletResponse();

    entryPoint.commence(
        new MockHttpServletRequest(),
        response,
        new BadCredentialsException("bad token"));

    assertThat(response.getStatus()).isEqualTo(401);
    assertThat(response.getContentAsString()).contains("\"status\":401");
    assertThat(response.getContentAsString()).contains("Authentication required");
  }
}
