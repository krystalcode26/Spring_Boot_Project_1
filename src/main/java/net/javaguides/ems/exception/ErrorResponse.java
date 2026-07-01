package net.javaguides.ems.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

//customized error response
@Getter
@AllArgsConstructor
public class ErrorResponse {
  private int status;
  private String message;
  private LocalDateTime timestamp;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Map<String, String> errors;
}
