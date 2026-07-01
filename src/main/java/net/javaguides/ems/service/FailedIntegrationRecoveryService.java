package net.javaguides.ems.service;

import net.javaguides.ems.dto.NameAggregationRequest;
import net.javaguides.ems.dto.NameAggregationResponse;
import net.javaguides.ems.entity.FailedIntegrationRequest;
import net.javaguides.ems.repository.FailedIntegrationRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FailedIntegrationRecoveryService {

  private static final Logger log = LoggerFactory.getLogger(FailedIntegrationRecoveryService.class);

  private final FailedIntegrationRequestRepository repository;
  private final RestClient restClient;

  @Value("${integration.recovery.enabled:true}")
  private boolean recoveryEnabled;

  @Value("${integration.recovery.max-attempts:5}")
  private int maxRecoveryAttempts;

  @Value("${downstream.application.url}")
  private String downstreamUrl;

  @Value("${downstream.application.path:/name/aggregation}")
  private String downstreamPath;

  public FailedIntegrationRecoveryService(
      FailedIntegrationRequestRepository repository,
      RestClient restClient) {
    this.repository = repository;
    this.restClient = restClient;
  }

  public void persistFailedDownstream(NameAggregationRequest request, String errorMessage) {
    if (!recoveryEnabled || request == null || request.getName() == null || request.getName().isEmpty()) {
      return;
    }
    FailedIntegrationRequest record = new FailedIntegrationRequest();
    record.setIntegrationType(FailedIntegrationRequest.TYPE_DOWNSTREAM);
    record.setPayloadJson(String.join(", ", request.getName()));
    record.setStatus(FailedIntegrationRequest.STATUS_PENDING);
    record.setAttemptCount(0);
    record.setLastError(errorMessage);
    record.setCreatedAt(LocalDateTime.now());
    repository.save(record);
    log.info("[Recovery] Persisted downstream request for names={}", request.getName());
  }

  @Scheduled(fixedDelayString = "${integration.recovery.fixed-delay-ms:60000}")
  public void replayPendingDownstreamRequests() {
    if (!recoveryEnabled) {
      return;
    }

    List<FailedIntegrationRequest> pending = repository.findByStatusAndIntegrationType(
        FailedIntegrationRequest.STATUS_PENDING,
        FailedIntegrationRequest.TYPE_DOWNSTREAM);

    if (pending.isEmpty()) {
      return;
    }

    log.info("[Recovery] Replaying {} pending downstream request(s)", pending.size());
    for (FailedIntegrationRequest record : pending) {
      replay(record);
    }
  }

  private void replay(FailedIntegrationRequest record) {
    record.setAttemptCount(record.getAttemptCount() + 1);
    try {
      NameAggregationRequest request = new NameAggregationRequest(parseNames(record.getPayloadJson()));
      restClient.post()
          .uri(downstreamUrl + downstreamPath)
          .body(request)
          .retrieve()
          .body(NameAggregationResponse.class);

      record.setStatus(FailedIntegrationRequest.STATUS_RECOVERED);
      record.setRecoveredAt(LocalDateTime.now());
      record.setLastError(null);
      log.info("[Recovery] Downstream request id={} recovered on attempt {}", record.getId(), record.getAttemptCount());
    } catch (Exception ex) {
      record.setLastError(ex.getMessage());
      if (record.getAttemptCount() >= maxRecoveryAttempts) {
        record.setStatus(FailedIntegrationRequest.STATUS_ABANDONED);
        log.warn("[Recovery] Downstream request id={} abandoned after {} attempts", record.getId(), record.getAttemptCount());
      } else {
        log.warn("[Recovery] Downstream request id={} retry {} failed: {}", record.getId(), record.getAttemptCount(), ex.getMessage());
      }
    }
    repository.save(record);
  }

  private List<String> parseNames(String payload) {
    return Arrays.stream(payload.split("\\s*,\\s*"))
        .filter(name -> !name.isBlank())
        .collect(Collectors.toList());
  }
}
