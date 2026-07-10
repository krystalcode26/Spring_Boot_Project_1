package net.javaguides.ems.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static net.javaguides.ems.testutil.SecurityTestSupport.bearerToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("auth")
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret",
    "security.jwt.secret=test-secret-key-for-unit-tests-only-must-be-long-enough"
})
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void me_withoutToken_returns401() throws Exception {
    mockMvc.perform(get("/api/auth/me"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void me_withBearerToken_returnsUserProfile() throws Exception {
    mockMvc.perform(get("/api/auth/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("user@example.com"))
        .andExpect(jsonPath("$.name").value("Test User"))
        .andExpect(jsonPath("$.roles[0]").value("USER"));
  }
}
