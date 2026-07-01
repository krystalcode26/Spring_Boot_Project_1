package net.javaguides.ems.service;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.aop.HandleNameAggregationFailure;
import net.javaguides.ems.client.DownstreamModuleClient;
import net.javaguides.ems.client.UpstreamModuleClient;
import net.javaguides.ems.dto.IntegrationChainResult;
import net.javaguides.ems.dto.NameAggregationRequest;
import net.javaguides.ems.dto.NameAggregationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class NameAggregationIntegrationExecutor {

  private final UpstreamModuleClient upstreamModuleClient;
  private final DownstreamModuleClient downstreamModuleClient;

  @Value("${integration.async.timeout-seconds:10}")
  private long asyncTimeoutSeconds;

  @Value("${downstream.application.enabled:true}")
  private boolean downstreamEnabled;

  @HandleNameAggregationFailure
  public IntegrationChainResult executeChain(NameAggregationRequest chainPayload, Set<String> localNames) {
    CompletableFuture<Void> upstreamFuture = upstreamModuleClient.notifyAsync(chainPayload);
    CompletableFuture<NameAggregationResponse> downstreamFuture = downstreamEnabled
        ? downstreamModuleClient.fetchNamesAsync(chainPayload)
        : CompletableFuture.completedFuture(buildLocalResponse(localNames));

    CompletableFuture.allOf(upstreamFuture, downstreamFuture)
        .orTimeout(asyncTimeoutSeconds, TimeUnit.SECONDS)
        .join();

    NameAggregationResponse downstreamResponse = downstreamFuture.join();
    if (!downstreamEnabled || downstreamResponse == null) {
      return IntegrationChainResult.success(new ArrayList<>());
    }
    if (downstreamResponse.getName() == null || downstreamResponse.getName().isEmpty()) {
      return IntegrationChainResult.failure("Downstream returned no names; returning local aggregation only.");
    }
    return IntegrationChainResult.success(downstreamResponse.getName());
  }

  private NameAggregationResponse buildLocalResponse(Set<String> aggregatedNames) {
    NameAggregationResponse response = new NameAggregationResponse();
    response.setName(new ArrayList<>(aggregatedNames));
    return response;
  }
}
