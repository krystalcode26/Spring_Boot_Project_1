package net.javaguides.ems.kafka;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.config.KafkaProperties;
import net.javaguides.ems.entity.Employee;
import net.javaguides.ems.mapper.EmployeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class EmployeeEventProducer {

  private static final Logger log = LoggerFactory.getLogger(EmployeeEventProducer.class);

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final KafkaProperties kafkaProperties;
  private final JsonMapper jsonMapper;

  public CompletableFuture<SendResult<String, String>> publish(EmployeeEventMessage message) {
    String key = message.employeeId() != null ? String.valueOf(message.employeeId()) : message.eventId();
    try {
      String payload = jsonMapper.writeValueAsString(message);
      log.info("Publishing employee event id={} type={} employeeId={} topic={}",
          message.eventId(), message.eventType(), message.employeeId(), kafkaProperties.topicEmployeeEvents());
      return kafkaTemplate.send(kafkaProperties.topicEmployeeEvents(), key, payload);
    } catch (Exception ex) {
      return CompletableFuture.failedFuture(ex);
    }
  }

  public CompletableFuture<SendResult<String, String>> publish(EmployeeEventType eventType, Employee employee) {
    return publish(new EmployeeEventMessage(
        UUID.randomUUID().toString(),
        eventType,
        employee.getEmpId(),
        EmployeeMapper.displayName(employee),
        employee.getEmail(),
        Instant.now()));
  }
}
