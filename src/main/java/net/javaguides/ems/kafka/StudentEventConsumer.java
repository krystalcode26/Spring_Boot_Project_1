package net.javaguides.ems.kafka;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.service.KafkaMessageRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class StudentEventConsumer {

  private static final Logger log = LoggerFactory.getLogger(StudentEventConsumer.class);

  private final KafkaMessageRegistry messageRegistry;
  private final JsonMapper jsonMapper;

  @KafkaListener(
      topics = "${kafka.topic-student-events}",
      groupId = "${kafka.consumer-group-id}",
      containerFactory = "studentEventKafkaListenerContainerFactory")
  public void consume(ConsumerRecord<String, String> record) {
    try {
      StudentEventMessage message = jsonMapper.readValue(record.value(), StudentEventMessage.class);
      log.info("Consumed student event id={} partition={} offset={} thread={}",
          message.eventId(), record.partition(), record.offset(), Thread.currentThread().getName());
      messageRegistry.register(record.partition(), record.offset(), message);
    } catch (Exception ex) {
      log.error("Failed to deserialize Kafka message at partition={} offset={}",
          record.partition(), record.offset(), ex);
    }
  }
}
