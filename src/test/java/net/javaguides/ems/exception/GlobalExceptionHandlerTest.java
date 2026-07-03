package net.javaguides.ems.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleNotFound_returns404Response() {
    ErrorResponse response = handler.handleNotFound(
        new ResourceNotFoundException("Student does not exist with given id: 1"));

    assertThat(response.getStatus()).isEqualTo(404);
    assertThat(response.getMessage()).contains("1");
    assertThat(response.getTimestamp()).isNotNull();
  }

  @Test
  void handleDownstream_returns502Response() {
    ErrorResponse response = handler.handleDownstream(
        new DownstreamServiceException("Downstream unavailable"));

    assertThat(response.getStatus()).isEqualTo(502);
    assertThat(response.getMessage()).isEqualTo("Downstream unavailable");
  }

  @Test
  void handleAll_returns500Response() {
    ErrorResponse response = handler.handleAll(new RuntimeException("boom"));

    assertThat(response.getStatus()).isEqualTo(500);
    assertThat(response.getMessage()).contains("boom");
  }
}
