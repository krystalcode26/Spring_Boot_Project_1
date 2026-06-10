package net.javaguides.ems.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

//customized error response
@Getter
@AllArgsConstructor
public class ErrorResponse {
  private int status;
  private String message;
  private LocalDateTime timestamp;
}
