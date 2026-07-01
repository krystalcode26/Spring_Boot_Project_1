package net.javaguides.ems.service.impl;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.dto.IntegrationChainResult;
import net.javaguides.ems.dto.NameAggregationRequest;
import net.javaguides.ems.dto.NameAggregationResponse;
import net.javaguides.ems.service.FailedIntegrationRecoveryService;
import net.javaguides.ems.service.NameAggregationIntegrationExecutor;
import net.javaguides.ems.service.NameAggregationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NameAggregationServiceImpl implements NameAggregationService {

  private static final Logger log = LoggerFactory.getLogger(NameAggregationServiceImpl.class);

  private final NameAggregationIntegrationExecutor integrationExecutor;
  private final FailedIntegrationRecoveryService failedIntegrationRecoveryService;

  @Value("${app.student.name:Krystal}")
  private String studentName;

  @Value("${downstream.application.enabled:true}")
  private boolean downstreamEnabled;

  @Override
  public NameAggregationResponse aggregate(NameAggregationRequest request) {
    Set<String> aggregatedNames = new LinkedHashSet<>();
    addNames(aggregatedNames, request.getName());

    aggregatedNames.add(studentName);
    log.info("[Chain] Added {} → current names={}", studentName, aggregatedNames);

    NameAggregationRequest chainPayload = new NameAggregationRequest(new ArrayList<>(aggregatedNames));

    IntegrationChainResult chainResult =
        integrationExecutor.executeChain(chainPayload, aggregatedNames);

    addNames(aggregatedNames, chainResult.getDownstreamNames());
    String warning = chainResult.getWarning();

    List<String> result = new ArrayList<>(aggregatedNames);
    log.info("[Chain] Final aggregated names={}", result);

    NameAggregationResponse response = new NameAggregationResponse();
    response.setName(result);
    response.setWarning(warning);
    if (warning != null && downstreamEnabled) {
      failedIntegrationRecoveryService.persistFailedDownstream(chainPayload, warning);
    }
    return response;
  }

  private void addNames(Set<String> aggregatedNames, List<String> names) {
    if (names == null) {
      return;
    }
    for (String name : names) {
      if (name != null && !name.isBlank()) {
        aggregatedNames.add(name.trim());
      }
    }
  }
}
