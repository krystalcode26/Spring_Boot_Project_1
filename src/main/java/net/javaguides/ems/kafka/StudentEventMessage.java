package net.javaguides.ems.kafka;

import java.time.Instant;

public record StudentEventMessage(
    String eventId,
    String eventType,
    Long studentId,
    String email,
    String firstName,
    String lastName,
    Instant occurredAt) {}
