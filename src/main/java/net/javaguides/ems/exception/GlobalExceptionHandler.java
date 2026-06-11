package net.javaguides.ems.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
/* @RestControllerAdvice - Spring Bean to claim the class become exception interceptor
 to intercept when there is any exception with this type(ResourceNotFoundException.class type),*/
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
    return new ErrorResponse(400, "Validation failed", LocalDateTime.now(), errors);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
    //customized exception(http status code, message of exception, specify time of throwing exceptions)
    return new ErrorResponse(404, ex.getMessage(), LocalDateTime.now(), null);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNoResource(NoResourceFoundException ex){
    return new ErrorResponse(404, ex.getMessage(), LocalDateTime.now(), null);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleAll(Exception ex){
    return new ErrorResponse(500, "Unexpected Error: " + ex.getMessage(), LocalDateTime.now(), null);
  }
}
