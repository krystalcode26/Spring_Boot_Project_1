package net.javaguides.ems.controller;

import net.javaguides.ems.dto.KafkaValidationResponse;
import net.javaguides.ems.kafka.EmployeeEventMessage;
import net.javaguides.ems.kafka.EmployeeEventProducer;
import net.javaguides.ems.kafka.EmployeeEventType;
import net.javaguides.ems.service.KafkaMessageRegistry;
import net.javaguides.ems.service.KafkaValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class KafkaControllerTest {

  @Mock
  private EmployeeEventProducer employeeEventProducer;

  @Mock
  private KafkaMessageRegistry messageRegistry;

  @Mock
  private KafkaValidationService kafkaValidationService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(
        new KafkaController(employeeEventProducer, messageRegistry, kafkaValidationService)).build();
  }

  @Test
  void publish_returnsAccepted() throws Exception {
    when(employeeEventProducer.publish(any())).thenReturn(CompletableFuture.completedFuture(null));

    mockMvc.perform(post("/api/kafka/messages")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "eventId":"evt-1",
                  "eventType":"CREATED",
                  "employeeId":1,
                  "employeeName":"Alice Smith",
                  "email":"alice@example.com",
                  "occurredAt":"2026-01-01T00:00:00Z"
                }
                """))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.eventId").value("evt-1"));

    verify(employeeEventProducer).publish(any(EmployeeEventMessage.class));
  }

  @Test
  void validate_returnsOk_whenSuccessful() throws Exception {
    KafkaValidationResponse response = new KafkaValidationResponse(
        true, 3, 3, List.of("a", "b", "c"), List.of());

    when(kafkaValidationService.validateProduceAndConsume()).thenReturn(response);

    mockMvc.perform(post("/api/kafka/validate"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.publishedCount").value(3));
  }

  @Test
  void consumedMessages_returnsRegistryContents() throws Exception {
    EmployeeEventMessage message = new EmployeeEventMessage(
        UUID.randomUUID().toString(), EmployeeEventType.VALIDATION, 1L, "Alice", "a@b.com", Instant.now());
    when(messageRegistry.getMessages())
        .thenReturn(List.of(new KafkaMessageRegistry.ConsumedMessage(0, 1L, message)));

    mockMvc.perform(get("/api/kafka/messages"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].partition").value(0))
        .andExpect(jsonPath("$[0].message.eventType").value("VALIDATION"));
  }
}
