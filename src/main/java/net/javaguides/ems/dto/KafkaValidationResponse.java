package net.javaguides.ems.dto;

import net.javaguides.ems.kafka.StudentEventMessage;

import java.util.List;

public record KafkaValidationResponse(
    boolean success,
    int publishedCount,
    int consumedCount,
    List<String> publishedEventIds,
    List<StudentEventMessage> consumedMessages) {}
