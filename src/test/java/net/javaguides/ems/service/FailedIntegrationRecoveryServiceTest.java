package net.javaguides.ems.service;

import net.javaguides.ems.dto.NameAggregationRequest;
import net.javaguides.ems.dto.NameAggregationResponse;
import net.javaguides.ems.entity.FailedIntegrationRequest;
import net.javaguides.ems.repository.FailedIntegrationRequestRepository;
import net.javaguides.ems.testutil.RestClientTestSupport;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.eq;
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

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(recoveryService, "recoveryEnabled", true);
    ReflectionTestUtils.setField(recoveryService, "maxRecoveryAttempts", 2);
    ReflectionTestUtils.setField(recoveryService, "downstreamUrl", "http://downstream");
    ReflectionTestUtils.setField(recoveryService, "downstreamPath", "/name/aggregation");
  }

  @Test
  void persistFailedDownstream_savesPendingRecord() {
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
  void persistFailedDownstream_skipsWhenRequestInvalid() {
    recoveryService.persistFailedDownstream(null, "error");
    recoveryService.persistFailedDownstream(new NameAggregationRequest(List.of()), "error");
    recoveryService.persistFailedDownstream(new NameAggregationRequest(null), "error");

    verifyNoInteractions(repository);
  }

  @Test
  void replayPendingDownstreamRequests_skipsWhenRecoveryDisabled() {
    ReflectionTestUtils.setField(recoveryService, "recoveryEnabled", false);

    recoveryService.replayPendingDownstreamRequests();

    verifyNoInteractions(repository);
    verifyNoInteractions(restClient);
  }

  @Test
  void replayPendingDownstreamRequests_skipsWhenNoPendingRecords() {
    when(repository.findByStatusAndIntegrationType(
        FailedIntegrationRequest.STATUS_PENDING,
        FailedIntegrationRequest.TYPE_DOWNSTREAM)).thenReturn(Collections.emptyList());

    recoveryService.replayPendingDownstreamRequests();

    verify(repository).findByStatusAndIntegrationType(
        FailedIntegrationRequest.STATUS_PENDING,
        FailedIntegrationRequest.TYPE_DOWNSTREAM);
    verifyNoInteractions(restClient);
  }

  @Test
  void replayPendingDownstreamRequests_recoversSuccessfulRequest() {
    FailedIntegrationRequest record = pendingRecord("Alice, Bob");
    when(repository.findByStatusAndIntegrationType(
        eq(FailedIntegrationRequest.STATUS_PENDING),
        eq(FailedIntegrationRequest.TYPE_DOWNSTREAM))).thenReturn(List.of(record));
    RestClientTestSupport.stubPostBody(restClient, new NameAggregationResponse(List.of("Alice", "Bob"), null));

    recoveryService.replayPendingDownstreamRequests();

    ArgumentCaptor<FailedIntegrationRequest> captor =
        ArgumentCaptor.forClass(FailedIntegrationRequest.class);
    verify(repository).save(captor.capture());
    assertThat(captor.getValue().getStatus()).isEqualTo(FailedIntegrationRequest.STATUS_RECOVERED);
    assertThat(captor.getValue().getRecoveredAt()).isNotNull();
    assertThat(captor.getValue().getLastError()).isNull();
  }

  @Test
  void replayPendingDownstreamRequests_retriesOnFailure() {
    FailedIntegrationRequest record = pendingRecord("Alice");
    when(repository.findByStatusAndIntegrationType(
        eq(FailedIntegrationRequest.STATUS_PENDING),
        eq(FailedIntegrationRequest.TYPE_DOWNSTREAM))).thenReturn(List.of(record));
    RestClientTestSupport.stubPostFailure(restClient, new RuntimeException("connection refused"));

    recoveryService.replayPendingDownstreamRequests();

    ArgumentCaptor<FailedIntegrationRequest> captor =
        ArgumentCaptor.forClass(FailedIntegrationRequest.class);
    verify(repository).save(captor.capture());
    assertThat(captor.getValue().getStatus()).isEqualTo(FailedIntegrationRequest.STATUS_PENDING);
    assertThat(captor.getValue().getAttemptCount()).isEqualTo(1);
    assertThat(captor.getValue().getLastError()).contains("connection refused");
  }

  @Test
  void replayPendingDownstreamRequests_abandonsAfterMaxAttempts() {
    FailedIntegrationRequest record = pendingRecord("Alice");
    record.setAttemptCount(1);
    when(repository.findByStatusAndIntegrationType(
        eq(FailedIntegrationRequest.STATUS_PENDING),
        eq(FailedIntegrationRequest.TYPE_DOWNSTREAM))).thenReturn(List.of(record));
    RestClientTestSupport.stubPostFailure(restClient, new RuntimeException("still down"));

    recoveryService.replayPendingDownstreamRequests();

    ArgumentCaptor<FailedIntegrationRequest> captor =
        ArgumentCaptor.forClass(FailedIntegrationRequest.class);
    verify(repository).save(captor.capture());
    assertThat(captor.getValue().getStatus()).isEqualTo(FailedIntegrationRequest.STATUS_ABANDONED);
    assertThat(captor.getValue().getAttemptCount()).isEqualTo(2);
  }

  private static FailedIntegrationRequest pendingRecord(String payload) {
    FailedIntegrationRequest record = new FailedIntegrationRequest();
    record.setId(1L);
    record.setIntegrationType(FailedIntegrationRequest.TYPE_DOWNSTREAM);
    record.setPayloadJson(payload);
    record.setStatus(FailedIntegrationRequest.STATUS_PENDING);
    record.setAttemptCount(0);
    return record;
  }
}
