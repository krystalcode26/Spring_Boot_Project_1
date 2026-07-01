package net.javaguides.ems.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import net.javaguides.ems.dto.NameAggregationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CompletableFuture;

@Component
public class UpstreamModuleClient {

  private static final Logger log = LoggerFactory.getLogger(UpstreamModuleClient.class);

  private final RestClient restClient;

  @Value("${upstream.application.enabled:false}")
  private boolean enabled;

  @Value("${upstream.application.url:http://localhost:8088}")
  private String upstreamUrl;

  @Value("${upstream.application.path:/name/notify}")
  private String upstreamPath;

  public UpstreamModuleClient(RestClient restClient) {
    this.restClient = restClient;
  }

  @Async("integrationExecutor")
  @CircuitBreaker(name = "upstreamModule", fallbackMethod = "notifyFallback")
  public CompletableFuture<Void> notifyAsync(NameAggregationRequest request) {
    if (!enabled) {
      log.debug("[Upstream] Notification skipped (upstream.application.enabled=false)");
      return CompletableFuture.completedFuture(null);
    }

    log.info("[Upstream] Notifying {}{} with names={}", upstreamUrl, upstreamPath, request.getName());

    restClient.post()
        .uri(upstreamUrl + upstreamPath)
        .body(request)
        .retrieve()
        .toBodilessEntity();

    return CompletableFuture.completedFuture(null);
  }

  @SuppressWarnings("unused")
  private CompletableFuture<Void> notifyFallback(NameAggregationRequest request, Throwable throwable) {
    log.warn("[Upstream] Circuit breaker open or call failed for names={}: {}",
        request.getName(), throwable.getMessage());
    return CompletableFuture.completedFuture(null);
  }
}
