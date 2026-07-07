package net.javaguides.ems.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static net.javaguides.ems.testutil.SecurityTestSupport.bearerToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "security.enabled=true",
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret",
    "security.jwt.secret=test-secret-key-for-unit-tests-only-must-be-long-enough"
})
class ApiSecurityTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void protectedApi_withoutToken_returns401() throws Exception {
    mockMvc.perform(get("/api/students"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401));
  }

  @Test
  void protectedApi_withBearerToken_returns200() throws Exception {
    mockMvc.perform(get("/api/students")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken()))
        .andExpect(status().isOk());
  }

  @Test
  void integrationEndpoint_withoutToken_isPublic() throws Exception {
    mockMvc.perform(get("/actuator/health"))
        .andExpect(status().isOk());
  }

  @Test
  void nameNotifyEndpoint_withoutToken_isPublic() throws Exception {
    mockMvc.perform(post("/name/notify")
            .contentType("application/json")
            .content("{\"name\":[\"Alice\"]}"))
        .andExpect(status().isOk());
  }
}
