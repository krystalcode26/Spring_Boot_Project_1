package net.javaguides.ems.exception;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.time.LocalDateTime;

@RestControllerAdvice
/* @RestControllerAdvice - Spring Bean to claim the class become exception interceptor
 to intercept when there is any exception with this type(ResourceNotFoundException.class type),*/
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
    //customized exception(http status code, message of exception, specify time of throwing exceptions)
    return new ErrorResponse(404, ex.getMessage(), LocalDateTime.now());
  }

  @ExceptionHandler(NoResourceFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNoResource(NoResourceFoundException ex){
    return new ErrorResponse(404, ex.getMessage(), LocalDateTime.now());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleAll(Exception ex){
    return new ErrorResponse(500, "Unexpected Error: " + ex.getMessage(), LocalDateTime.now());
  }
}
