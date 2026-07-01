package net.javaguides.ems.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class StudentNotificationService {

  private static final Logger log = LoggerFactory.getLogger(StudentNotificationService.class);

  @Async
  public void sendWelcomeNotification(String email) {
    try {
      log.info("[Async] Sending welcome notification to {}", email);
      Thread.sleep(2000);
      log.info("[Async] Welcome notification sent to {}", email);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.warn("[Async] Notification interrupted for {}", email);
    }
  }
}
