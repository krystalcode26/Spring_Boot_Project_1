package net.javaguides.ems.smoke;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

@Tag("smoke")
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationSmokeTest {

  @Autowired
  private RestTestClient restClient;

  @Test
  void applicationContextLoads() {
    // passes if Spring context starts
  }

  @Test
  void actuatorHealth_returnsUp() {
    restClient.get()
        .uri("/actuator/health")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.status").isEqualTo("UP");
  }
}
