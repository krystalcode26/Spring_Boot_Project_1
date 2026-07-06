package net.javaguides.ems.aop;

import net.javaguides.ems.dto.IntegrationChainResult;
import net.javaguides.ems.exception.DownstreamServiceException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NameAggregationExceptionAspectTest {

  @Mock
  private ProceedingJoinPoint joinPoint;

  @InjectMocks
  private NameAggregationExceptionAspect aspect;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(aspect, "asyncTimeoutSeconds", 10L);
    ReflectionTestUtils.setField(aspect, "downstreamEnabled", true);
  }

  @Test
  void handleIntegrationFailure_returnsResultOnSuccess() throws Throwable {
    IntegrationChainResult success = IntegrationChainResult.success(java.util.List.of("Alice"));
    when(joinPoint.proceed()).thenReturn(success);

    Object result = aspect.handleIntegrationFailure(joinPoint);

    assertThat(result).isSameAs(success);
  }

  @Test
  void handleIntegrationFailure_returnsWarningForDownstreamException() throws Throwable {
    when(joinPoint.proceed()).thenThrow(new CompletionException(
        new DownstreamServiceException("Downstream unavailable")));

    Object result = aspect.handleIntegrationFailure(joinPoint);

    assertThat(result).isInstanceOf(IntegrationChainResult.class);
    IntegrationChainResult chainResult = (IntegrationChainResult) result;
    assertThat(chainResult.getWarning()).isEqualTo("Downstream unavailable");
    assertThat(chainResult.getDownstreamNames()).isEmpty();
  }

  @Test
  void handleIntegrationFailure_returnsTimeoutMessage() throws Throwable {
    when(joinPoint.proceed()).thenThrow(new TimeoutException("timed out"));

    IntegrationChainResult result = (IntegrationChainResult) aspect.handleIntegrationFailure(joinPoint);

    assertThat(result.getWarning()).contains("Integration timed out after 10s");
  }

  @Test
  void handleIntegrationFailure_returnsGenericWarningWhenDownstreamDisabled() throws Throwable {
    ReflectionTestUtils.setField(aspect, "downstreamEnabled", false);
    when(joinPoint.proceed()).thenThrow(new ExecutionException(new RuntimeException("boom")));

    IntegrationChainResult result = (IntegrationChainResult) aspect.handleIntegrationFailure(joinPoint);

    assertThat(result.getWarning()).isNull();
  }

  @Test
  void handleIntegrationFailure_handlesInterruptedException() throws Throwable {
    when(joinPoint.proceed()).thenThrow(new InterruptedException("interrupted"));

    IntegrationChainResult result = (IntegrationChainResult) aspect.handleIntegrationFailure(joinPoint);

    assertThat(result.getWarning())
        .isEqualTo("Downstream integration failed; returning local aggregation only.");
    assertThat(Thread.currentThread().isInterrupted()).isTrue();
    assertThat(Thread.interrupted()).isTrue();
  }
}
