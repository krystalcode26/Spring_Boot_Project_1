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
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
@EnableConfigurationProperties(net.javaguides.ems.config.KafkaProperties.class)
public class KafkaConfig {

  @Bean
  NewTopic studentEventsTopic(net.javaguides.ems.config.KafkaProperties kafkaProperties) {
    return TopicBuilder.name(kafkaProperties.topicStudentEvents())
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
  ConcurrentKafkaListenerContainerFactory<String, String> studentEventKafkaListenerContainerFactory(
      ConsumerFactory<String, String> kafkaConsumerFactory,
      net.javaguides.ems.config.KafkaProperties kafkaProperties) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(kafkaConsumerFactory);
    factory.setConcurrency(kafkaProperties.consumerConcurrency());
    return factory;
  }
}
