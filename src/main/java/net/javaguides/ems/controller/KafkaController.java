package net.javaguides.ems.controller;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.dto.KafkaValidationResponse;
import net.javaguides.ems.kafka.StudentEventMessage;
import net.javaguides.ems.kafka.StudentEventProducer;
import net.javaguides.ems.service.KafkaMessageRegistry;
import net.javaguides.ems.service.KafkaValidationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class KafkaController {

  private final StudentEventProducer studentEventProducer;
  private final KafkaMessageRegistry messageRegistry;
  private final KafkaValidationService kafkaValidationService;

  @PostMapping("/messages")
  public ResponseEntity<StudentEventMessage> publish(@RequestBody StudentEventMessage message) {
    studentEventProducer.publish(message).join();
    return ResponseEntity.accepted().body(message);
  }

  @GetMapping("/messages")
  public ResponseEntity<List<KafkaMessageRegistry.ConsumedMessage>> consumedMessages() {
    return ResponseEntity.ok(messageRegistry.getMessages());
  }

  @PostMapping("/validate")
  public ResponseEntity<KafkaValidationResponse> validate() {
    KafkaValidationResponse response = kafkaValidationService.validateProduceAndConsume();
    return response.success()
        ? ResponseEntity.ok(response)
        : ResponseEntity.status(503).body(response);
  }
}
