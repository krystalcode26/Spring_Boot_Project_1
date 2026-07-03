package net.javaguides.ems.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DemoServiceTest {

  private final DemoService demoService = new DemoService();

  @Test
  void triggerProblemA_returnsExpectedMessage() {
    assertThat(demoService.triggerProblemA())
        .contains("Problem A done");
  }

  @Test
  void triggerProblemB_returnsExpectedMessage() {
    assertThat(demoService.triggerProblemB())
        .contains("Problem B done");
  }
}
