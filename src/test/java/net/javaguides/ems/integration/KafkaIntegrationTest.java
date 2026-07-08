package net.javaguides.ems.integration;

import net.javaguides.ems.dto.KafkaValidationResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@EmbeddedKafka(partitions = 3, topics = {"student-events"})
@TestPropertySource(properties = {
    "kafka.enabled=true",
    "kafka.topic-student-events=student-events",
    "kafka.topic-partitions=3",
    "kafka.topic-replicas=1",
    "kafka.consumer-group-id=ems-kafka-integration-test",
    "kafka.consumer-concurrency=3",
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.consumer.group-id=ems-kafka-integration-test",
    "spring.kafka.consumer.auto-offset-reset=earliest"
})
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KafkaIntegrationTest {

  @Autowired
  private RestTestClient restClient;

  @Test
  void validateEndpoint_producesAndConsumesMessages() {
    restClient.post()
        .uri("/api/kafka/validate")
        .exchange()
        .expectStatus().isOk()
        .expectBody(KafkaValidationResponse.class)
        .value(response -> {
          assertThat(response.success()).isTrue();
          assertThat(response.publishedCount()).isEqualTo(3);
          assertThat(response.consumedCount()).isEqualTo(3);
          assertThat(response.consumedMessages()).hasSize(3);
        });
  }
}
