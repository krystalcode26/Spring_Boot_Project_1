package net.javaguides.ems.service;

import net.javaguides.ems.kafka.EmployeeEventMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class KafkaMessageRegistry {

  private final CopyOnWriteArrayList<ConsumedMessage> messages = new CopyOnWriteArrayList<>();
  private final ConcurrentMap<String, Boolean> eventIds = new ConcurrentHashMap<>();

  public void register(int partition, long offset, EmployeeEventMessage message) {
    if (eventIds.putIfAbsent(message.eventId(), Boolean.TRUE) != null) {
      return;
    }
    messages.add(new ConsumedMessage(partition, offset, message));
  }

  public List<ConsumedMessage> getMessages() {
    return List.copyOf(messages);
  }

  public void clear() {
    messages.clear();
    eventIds.clear();
  }

  public boolean containsEventId(String eventId) {
    return eventIds.containsKey(eventId);
  }

  public record ConsumedMessage(int partition, long offset, EmployeeEventMessage message) {}
}
