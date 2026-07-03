package net.javaguides.ems.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrationChainResultTest {

  @Test
  void success_hasNoWarning() {
    IntegrationChainResult result = IntegrationChainResult.success(List.of("Alice", "Bob"));

    assertThat(result.getDownstreamNames()).containsExactly("Alice", "Bob");
    assertThat(result.getWarning()).isNull();
    assertThat(result.hasWarning()).isFalse();
  }

  @Test
  void failure_hasWarningAndEmptyNames() {
    IntegrationChainResult result = IntegrationChainResult.failure("Downstream unavailable");

    assertThat(result.getDownstreamNames()).isEmpty();
    assertThat(result.getWarning()).isEqualTo("Downstream unavailable");
    assertThat(result.hasWarning()).isTrue();
  }
}
