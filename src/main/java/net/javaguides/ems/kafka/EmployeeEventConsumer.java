package net.javaguides.ems.kafka;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.service.KafkaMessageRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class EmployeeEventConsumer {

  private static final Logger log = LoggerFactory.getLogger(EmployeeEventConsumer.class);

  private final KafkaMessageRegistry messageRegistry;
  private final JsonMapper jsonMapper;

  @KafkaListener(
      topics = "${kafka.topic-employee-events}",
      groupId = "${kafka.consumer-group-id}",
      containerFactory = "employeeEventKafkaListenerContainerFactory")
  public void onEvent(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    try {
      EmployeeEventMessage message = jsonMapper.readValue(record.value(), EmployeeEventMessage.class);
      log.info("[{}] consumed {} for employee {} partition={} offset={} thread={}",
          "notification-service",
          message.eventType(),
          message.employeeId(),
          record.partition(),
          record.offset(),
          Thread.currentThread().getName());

      messageRegistry.register(record.partition(), record.offset(), message);
      acknowledgment.acknowledge();
    } catch (Exception ex) {
      log.error("Failed to process employee event partition={} offset={}",
          record.partition(), record.offset(), ex);
      throw new IllegalArgumentException("Unable to process employee event", ex);
    }
  }
}
