package net.javaguides.ems.aop;

import net.javaguides.ems.dto.IntegrationChainResult;
import net.javaguides.ems.exception.DownstreamServiceException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Aspect
@Component
public class NameAggregationExceptionAspect {

  private static final Logger log = LoggerFactory.getLogger(NameAggregationExceptionAspect.class);

  @Value("${integration.async.timeout-seconds:10}")
  private long asyncTimeoutSeconds;

  @Value("${downstream.application.enabled:true}")
  private boolean downstreamEnabled;

  @Around("@annotation(HandleNameAggregationFailure)")
  public Object handleIntegrationFailure(ProceedingJoinPoint joinPoint) {
    try {
      return joinPoint.proceed();
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      log.error("Async module integration interrupted: {}", ex.getMessage());
      return IntegrationChainResult.failure(resolveDownstreamWarning(ex));
    } catch (TimeoutException ex) {
      log.error("Async module integration timed out: {}", ex.getMessage());
      return IntegrationChainResult.failure(
          "Integration timed out after " + asyncTimeoutSeconds + "s; returning local aggregation only.");
    } catch (ExecutionException ex) {
      log.error("Async module integration failed: {}", ex.getMessage());
      return IntegrationChainResult.failure(resolveDownstreamWarning(ex));
    } catch (CompletionException ex) {
      log.error("Async module integration failed: {}", ex.getMessage());
      return IntegrationChainResult.failure(resolveDownstreamWarning(ex));
    } catch (Throwable ex) {
      log.error("Async module integration failed unexpectedly: {}", ex.getMessage());
      return IntegrationChainResult.failure(resolveDownstreamWarning(ex));
    }
  }

  private String resolveDownstreamWarning(Throwable ex) {
    Throwable cause = unwrap(ex);
    if (cause instanceof DownstreamServiceException downstreamEx) {
      return downstreamEx.getMessage();
    }
    if (downstreamEnabled) {
      return "Downstream integration failed; returning local aggregation only.";
    }
    return null;
  }

  private Throwable unwrap(Throwable ex) {
    Throwable current = ex;
    while (current.getCause() != null && current.getCause() != current) {
      current = current.getCause();
    }
    return current;
  }
}
