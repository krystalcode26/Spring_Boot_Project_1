package net.javaguides.ems.service;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.dto.KafkaValidationResponse;
import net.javaguides.ems.kafka.EmployeeEventMessage;
import net.javaguides.ems.kafka.EmployeeEventProducer;
import net.javaguides.ems.kafka.EmployeeEventType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.LockSupport;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class KafkaValidationService {

  private static final int VALIDATION_MESSAGE_COUNT = 3;
  private static final long POLL_INTERVAL_MS = 200L;
  private static final long TIMEOUT_MS = 15_000L;

  private final EmployeeEventProducer employeeEventProducer;
  private final KafkaMessageRegistry messageRegistry;

  public KafkaValidationResponse validateProduceAndConsume() {
    messageRegistry.clear();
    List<String> publishedEventIds = new ArrayList<>();

    for (int index = 0; index < VALIDATION_MESSAGE_COUNT; index++) {
      String eventId = UUID.randomUUID().toString();
      publishedEventIds.add(eventId);
      EmployeeEventMessage message = new EmployeeEventMessage(
          eventId,
          EmployeeEventType.VALIDATION,
          10_000L + index,
          "Kafka Test " + index,
          "kafka-test-" + index + "@ems.com",
          Instant.now());
      employeeEventProducer.publish(message).join();
    }

    long deadline = System.currentTimeMillis() + TIMEOUT_MS;
    while (System.currentTimeMillis() < deadline) {
      if (publishedEventIds.stream().allMatch(messageRegistry::containsEventId)) {
        break;
      }
      if (Thread.currentThread().isInterrupted()) {
        break;
      }
      LockSupport.parkNanos(POLL_INTERVAL_MS * 1_000_000L);
    }

    List<KafkaMessageRegistry.ConsumedMessage> consumed = messageRegistry.getMessages();
    boolean success = publishedEventIds.stream().allMatch(messageRegistry::containsEventId);
    return new KafkaValidationResponse(
        success,
        publishedEventIds.size(),
        consumed.size(),
        publishedEventIds,
        consumed.stream().map(consumedMessage -> consumedMessage.message()).toList());
  }
}
