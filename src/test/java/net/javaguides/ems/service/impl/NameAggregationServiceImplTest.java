package net.javaguides.ems.service.impl;

import net.javaguides.ems.dto.IntegrationChainResult;
import net.javaguides.ems.dto.NameAggregationRequest;
import net.javaguides.ems.dto.NameAggregationResponse;
import net.javaguides.ems.service.FailedIntegrationRecoveryService;
import net.javaguides.ems.service.NameAggregationIntegrationExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NameAggregationServiceImplTest {

  @Mock
  private NameAggregationIntegrationExecutor integrationExecutor;

  @Mock
  private FailedIntegrationRecoveryService failedIntegrationRecoveryService;

  @InjectMocks
  private NameAggregationServiceImpl nameAggregationService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(nameAggregationService, "studentName", "Test");
    ReflectionTestUtils.setField(nameAggregationService, "downstreamEnabled", true);
  }

  @Test
  void aggregate_returnsLocalNamesAndWarningWhenChainFails() {
    when(integrationExecutor.executeChain(any(), any()))
        .thenReturn(IntegrationChainResult.failure("Downstream unavailable"));

    NameAggregationResponse response =
        nameAggregationService.aggregate(new NameAggregationRequest(List.of("Alice")));

    assertThat(response.getName()).containsExactly("Alice", "Test");
    assertThat(response.getWarning()).isEqualTo("Downstream unavailable");
    verify(failedIntegrationRecoveryService).persistFailedDownstream(any(), eq("Downstream unavailable"));
  }

  @Test
  void aggregate_mergesDownstreamNamesWhenChainSucceeds() {
    when(integrationExecutor.executeChain(any(), any()))
        .thenReturn(IntegrationChainResult.success(List.of("Bob")));

    NameAggregationResponse response =
        nameAggregationService.aggregate(new NameAggregationRequest(List.of("Alice")));

    assertThat(response.getName()).containsExactly("Alice", "Test", "Bob");
    assertThat(response.getWarning()).isNull();
    verify(failedIntegrationRecoveryService, never()).persistFailedDownstream(any(), any());
  }
}
