package net.javaguides.ems.service;

import net.javaguides.ems.kafka.EmployeeEventMessage;
import net.javaguides.ems.kafka.EmployeeEventProducer;
import net.javaguides.ems.kafka.EmployeeEventType;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaValidationServiceTest {

  @Mock
  private EmployeeEventProducer employeeEventProducer;

  private KafkaMessageRegistry messageRegistry;
  private KafkaValidationService kafkaValidationService;

  @BeforeEach
  void setUp() {
    messageRegistry = new KafkaMessageRegistry();
    kafkaValidationService = new KafkaValidationService(employeeEventProducer, messageRegistry);
  }

  @Test
  void validateProduceAndConsume_returnsSuccess_whenAllMessagesConsumed() {
    when(employeeEventProducer.publish(any())).thenAnswer(invocation -> {
      EmployeeEventMessage message = invocation.getArgument(0);
      messageRegistry.register(0, 0L, message);
      return CompletableFuture.completedFuture(mock(SendResult.class));
    });

    var response = kafkaValidationService.validateProduceAndConsume();

    assertThat(response.success()).isTrue();
    assertThat(response.publishedCount()).isEqualTo(3);
    assertThat(response.consumedCount()).isEqualTo(3);
    assertThat(response.consumedMessages()).hasSize(3);
  }

  @Test
  void register_deduplicatesByEventId() {
    EmployeeEventMessage message = new EmployeeEventMessage(
        UUID.randomUUID().toString(), EmployeeEventType.VALIDATION, 1L, "Alice", "a@b.com", Instant.now());

    messageRegistry.register(0, 1L, message);
    messageRegistry.register(1, 2L, message);

    assertThat(messageRegistry.getMessages()).hasSize(1);
  }
}
