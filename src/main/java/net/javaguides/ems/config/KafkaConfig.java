package net.javaguides.ems.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
@EnableConfigurationProperties(net.javaguides.ems.config.KafkaProperties.class)
public class KafkaConfig {

  @Bean
  NewTopic employeeEventsTopic(net.javaguides.ems.config.KafkaProperties kafkaProperties) {
    return TopicBuilder.name(kafkaProperties.topicEmployeeEvents())
        .partitions(kafkaProperties.topicPartitions())
        .replicas(kafkaProperties.topicReplicas())
        .build();
  }

  @Bean
  NewTopic employeeEventsDltTopic(net.javaguides.ems.config.KafkaProperties kafkaProperties) {
    return TopicBuilder.name(kafkaProperties.topicEmployeeEventsDlt())
        .partitions(kafkaProperties.topicPartitions())
        .replicas(kafkaProperties.topicReplicas())
        .build();
  }

  @Bean
  ProducerFactory<String, String> kafkaProducerFactory(
      org.springframework.boot.kafka.autoconfigure.KafkaProperties springKafkaProperties) {
    Map<String, Object> config = new HashMap<>(springKafkaProperties.buildProducerProperties());
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return new DefaultKafkaProducerFactory<>(config);
  }

  @Bean
  KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> kafkaProducerFactory) {
    return new KafkaTemplate<>(kafkaProducerFactory);
  }

  @Bean
  ConsumerFactory<String, String> kafkaConsumerFactory(
      org.springframework.boot.kafka.autoconfigure.KafkaProperties springKafkaProperties) {
    Map<String, Object> config = new HashMap<>(springKafkaProperties.buildConsumerProperties());
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    return new DefaultKafkaConsumerFactory<>(config);
  }

  @Bean
  DefaultErrorHandler kafkaErrorHandler(
      KafkaTemplate<String, String> kafkaTemplate,
      net.javaguides.ems.config.KafkaProperties kafkaProperties) {
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
        kafkaTemplate,
        (record, exception) -> new org.apache.kafka.common.TopicPartition(
            kafkaProperties.topicEmployeeEventsDlt(),
            record.partition()));

    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
    errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
    return errorHandler;
  }

  @Bean
  ConcurrentKafkaListenerContainerFactory<String, String> employeeEventKafkaListenerContainerFactory(
      ConsumerFactory<String, String> kafkaConsumerFactory,
      net.javaguides.ems.config.KafkaProperties kafkaProperties,
      DefaultErrorHandler kafkaErrorHandler) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(kafkaConsumerFactory);
    factory.setConcurrency(kafkaProperties.consumerConcurrency());
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    factory.setCommonErrorHandler(kafkaErrorHandler);
    return factory;
  }
}
