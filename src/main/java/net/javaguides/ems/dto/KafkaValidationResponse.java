package net.javaguides.ems.dto;

import net.javaguides.ems.kafka.EmployeeEventMessage;

import java.util.List;

public record KafkaValidationResponse(
    boolean success,
    int publishedCount,
    int consumedCount,
    List<String> publishedEventIds,
    List<EmployeeEventMessage> consumedMessages) {}
