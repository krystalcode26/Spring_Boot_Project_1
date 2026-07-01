package net.javaguides.ems.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import net.javaguides.ems.dto.NameAggregationRequest;
import net.javaguides.ems.dto.NameAggregationResponse;
import net.javaguides.ems.exception.DownstreamServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CompletableFuture;

@Component
public class DownstreamModuleClient {

  private static final Logger log = LoggerFactory.getLogger(DownstreamModuleClient.class);

  private final RestClient restClient;

  @Value("${downstream.application.url}")
  private String downstreamUrl;

  @Value("${downstream.application.path:/name/aggregation}")
  private String downstreamPath;

  public DownstreamModuleClient(RestClient restClient) {
    this.restClient = restClient;
  }

  @Async("integrationExecutor")
  @Retry(name = "downstreamModule")
  @CircuitBreaker(name = "downstreamModule", fallbackMethod = "fetchNamesFallback")
  public CompletableFuture<NameAggregationResponse> fetchNamesAsync(NameAggregationRequest request) {
    log.info("[Downstream] Calling {}{} with names={}", downstreamUrl, downstreamPath, request.getName());

    NameAggregationResponse response = restClient.post()
        .uri(downstreamUrl + downstreamPath)
        .body(request)
        .retrieve()
        .body(NameAggregationResponse.class);

    return CompletableFuture.completedFuture(response);
  }

  @SuppressWarnings("unused")
  private CompletableFuture<NameAggregationResponse> fetchNamesFallback(
      NameAggregationRequest request, Throwable throwable) {
    log.warn("[Downstream] Circuit breaker open or call failed for names={}: {}",
        request.getName(), throwable.getMessage());
    return CompletableFuture.failedFuture(
        new DownstreamServiceException("Downstream unavailable: " + throwable.getMessage(), throwable));
  }
}
