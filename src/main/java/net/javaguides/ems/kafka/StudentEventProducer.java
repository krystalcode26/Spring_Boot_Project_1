package net.javaguides.ems.kafka;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.config.KafkaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class StudentEventProducer {

  private static final Logger log = LoggerFactory.getLogger(StudentEventProducer.class);

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final KafkaProperties kafkaProperties;
  private final JsonMapper jsonMapper;

  public CompletableFuture<SendResult<String, String>> publish(StudentEventMessage message) {
    String key = message.studentId() != null ? String.valueOf(message.studentId()) : message.eventId();
    try {
      String payload = jsonMapper.writeValueAsString(message);
      log.info("Publishing student event id={} type={} to topic={}",
          message.eventId(), message.eventType(), kafkaProperties.topicStudentEvents());
      return kafkaTemplate.send(kafkaProperties.topicStudentEvents(), key, payload);
    } catch (Exception ex) {
      return CompletableFuture.failedFuture(ex);
    }
  }
}
