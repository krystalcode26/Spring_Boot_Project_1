package net.javaguides.ems.kafka;

import java.time.Instant;

public record EmployeeEventMessage(
    String eventId,
    EmployeeEventType eventType,
    Long employeeId,
    String employeeName,
    String email,
    Instant occurredAt) {}
