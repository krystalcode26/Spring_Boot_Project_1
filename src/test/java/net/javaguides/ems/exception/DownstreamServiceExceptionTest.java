package net.javaguides.ems.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DownstreamServiceExceptionTest {

  @Test
  void constructors_storeMessageAndCause() {
    RuntimeException cause = new RuntimeException("root");

    DownstreamServiceException withMessage =
        new DownstreamServiceException("Downstream unavailable");
    DownstreamServiceException withCause =
        new DownstreamServiceException("wrapped", cause);

    assertThat(withMessage.getMessage()).isEqualTo("Downstream unavailable");
    assertThat(withCause.getMessage()).isEqualTo("wrapped");
    assertThat(withCause.getCause()).isSameAs(cause);
  }
}
