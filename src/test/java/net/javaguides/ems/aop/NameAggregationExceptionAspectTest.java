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
  void handleIntegrationFailure_returnsWarningForDownstreamException() throws Throwable {
    when(joinPoint.proceed()).thenThrow(new CompletionException(
        new DownstreamServiceException("Downstream unavailable")));

    Object result = aspect.handleIntegrationFailure(joinPoint);

    assertThat(result).isInstanceOf(IntegrationChainResult.class);
    IntegrationChainResult chainResult = (IntegrationChainResult) result;
    assertThat(chainResult.getWarning()).isEqualTo("Downstream unavailable");
    assertThat(chainResult.getDownstreamNames()).isEmpty();
  }
}
