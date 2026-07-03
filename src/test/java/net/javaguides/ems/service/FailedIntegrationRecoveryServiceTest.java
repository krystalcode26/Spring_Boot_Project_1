package net.javaguides.ems.service;

import net.javaguides.ems.dto.NameAggregationRequest;
import net.javaguides.ems.entity.FailedIntegrationRequest;
import net.javaguides.ems.repository.FailedIntegrationRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FailedIntegrationRecoveryServiceTest {

  @Mock
  private FailedIntegrationRequestRepository repository;

  @Mock
  private RestClient restClient;

  @InjectMocks
  private FailedIntegrationRecoveryService recoveryService;

  @Test
  void persistFailedDownstream_savesPendingRecord() {
    ReflectionTestUtils.setField(recoveryService, "recoveryEnabled", true);

    NameAggregationRequest request = new NameAggregationRequest(List.of("Alice", "Test"));

    recoveryService.persistFailedDownstream(request, "Downstream unavailable");

    ArgumentCaptor<FailedIntegrationRequest> captor =
        ArgumentCaptor.forClass(FailedIntegrationRequest.class);
    verify(repository).save(captor.capture());
    verifyNoInteractions(restClient);

    FailedIntegrationRequest saved = captor.getValue();
    assertThat(saved.getStatus()).isEqualTo(FailedIntegrationRequest.STATUS_PENDING);
    assertThat(saved.getIntegrationType()).isEqualTo(FailedIntegrationRequest.TYPE_DOWNSTREAM);
    assertThat(saved.getPayloadJson()).contains("Alice");
    assertThat(saved.getLastError()).isEqualTo("Downstream unavailable");
  }

  @Test
  void persistFailedDownstream_skipsWhenRecoveryDisabled() {
    ReflectionTestUtils.setField(recoveryService, "recoveryEnabled", false);

    recoveryService.persistFailedDownstream(
        new NameAggregationRequest(List.of("Alice")),
        "Downstream unavailable");

    verifyNoInteractions(repository);
    verifyNoInteractions(restClient);
  }

  @Test
  void replayPendingDownstreamRequests_skipsWhenRecoveryDisabled() {
    ReflectionTestUtils.setField(recoveryService, "recoveryEnabled", false);

    recoveryService.replayPendingDownstreamRequests();

    verifyNoInteractions(repository);
    verifyNoInteractions(restClient);
  }

  @Test
  void replayPendingDownstreamRequests_doesNothingWhenNoPendingRows() {
    ReflectionTestUtils.setField(recoveryService, "recoveryEnabled", true);
    when(repository.findByStatusAndIntegrationType(
        FailedIntegrationRequest.STATUS_PENDING,
        FailedIntegrationRequest.TYPE_DOWNSTREAM))
        .thenReturn(Collections.emptyList());

    recoveryService.replayPendingDownstreamRequests();

    verify(repository).findByStatusAndIntegrationType(
        FailedIntegrationRequest.STATUS_PENDING,
        FailedIntegrationRequest.TYPE_DOWNSTREAM);
    verifyNoInteractions(restClient);
  }
}
