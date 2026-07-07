package net.javaguides.ems.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudentNotificationServiceTest {

  private final StudentNotificationService service = new StudentNotificationService();

  @Test
  void sendWelcomeNotification_handlesInterrupt() throws InterruptedException {
    Thread worker = new Thread(() -> service.sendWelcomeNotification("student@example.com"));
    worker.start();
    worker.interrupt();
    worker.join(5_000);

    assertThat(worker.isAlive()).isFalse();
  }

  @Test
  void sendWelcomeNotification_completesSuccessfully() throws InterruptedException {
    Thread worker = new Thread(() -> service.sendWelcomeNotification("student@example.com"));
    worker.start();
    worker.join(5_000);

    assertThat(worker.isAlive()).isFalse();
  }
}
