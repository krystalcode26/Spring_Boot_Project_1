package net.javaguides.ems.service;

import net.javaguides.ems.kafka.StudentEventMessage;
import net.javaguides.ems.kafka.StudentEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaValidationServiceTest {

  @Mock
  private StudentEventProducer studentEventProducer;

  private KafkaMessageRegistry messageRegistry;
  private KafkaValidationService kafkaValidationService;

  @BeforeEach
  void setUp() {
    messageRegistry = new KafkaMessageRegistry();
    kafkaValidationService = new KafkaValidationService(studentEventProducer, messageRegistry);
  }

  @Test
  void validateProduceAndConsume_returnsSuccess_whenAllMessagesConsumed() {
    when(studentEventProducer.publish(any())).thenAnswer(invocation -> {
      StudentEventMessage message = invocation.getArgument(0);
      messageRegistry.register(0, 0L, message);
      return CompletableFuture.completedFuture(new SendResult<>(null, null));
    });

    var response = kafkaValidationService.validateProduceAndConsume();

    assertThat(response.success()).isTrue();
    assertThat(response.publishedCount()).isEqualTo(3);
    assertThat(response.consumedCount()).isEqualTo(3);
    assertThat(response.consumedMessages()).hasSize(3);
  }

  @Test
  void register_deduplicatesByEventId() {
    StudentEventMessage message = new StudentEventMessage(
        UUID.randomUUID().toString(), "TEST", 1L, "a@b.com", "A", "B", Instant.now());

    messageRegistry.register(0, 1L, message);
    messageRegistry.register(1, 2L, message);

    assertThat(messageRegistry.getMessages()).hasSize(1);
  }
}
