package net.javaguides.ems.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "false")
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
public class KafkaDisabledConfig {}
