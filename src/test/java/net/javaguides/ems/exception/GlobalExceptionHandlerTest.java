package net.javaguides.ems.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
  void handleNoResource_returns404Response() {
    ErrorResponse response = handler.handleNoResource(
        new NoResourceFoundException(HttpMethod.GET, "/missing", "Not found"));

    assertThat(response.getStatus()).isEqualTo(404);
    assertThat(response.getMessage()).isNotNull();
  }

  @Test
  void handleValidation_returns400WithFieldErrors() throws NoSuchMethodException {
    Object target = new Object();
    BeanPropertyBindingResult bindingResult =
        new BeanPropertyBindingResult(target, "studentDto");
    bindingResult.addError(new FieldError("studentDto", "email", "must not be blank"));
    MethodParameter parameter = new MethodParameter(
        GlobalExceptionHandlerTest.class.getDeclaredMethod("validationTarget", Object.class), 0);
    MethodArgumentNotValidException ex =
        new MethodArgumentNotValidException(parameter, bindingResult);

    ErrorResponse response = handler.handleValidation(ex);

    assertThat(response.getStatus()).isEqualTo(400);
    assertThat(response.getMessage()).isEqualTo("Validation failed");
    assertThat(response.getErrors()).containsEntry("email", "must not be blank");
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

  @SuppressWarnings("unused")
  private void validationTarget(Object ignored) {
    // helper for MethodParameter in handleValidation test
  }
}
