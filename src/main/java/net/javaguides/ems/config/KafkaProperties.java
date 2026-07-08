package net.javaguides.ems.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record KafkaProperties(
    boolean enabled,
    String topicEmployeeEvents,
    String topicEmployeeEventsDlt,
    int topicPartitions,
    short topicReplicas,
    String consumerGroupId,
    int consumerConcurrency) {}
