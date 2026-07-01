package net.javaguides.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class IntegrationChainResult {

  private final List<String> downstreamNames;
  private final String warning;

  public static IntegrationChainResult success(List<String> downstreamNames) {
    return new IntegrationChainResult(downstreamNames, null);
  }

  public static IntegrationChainResult failure(String warning) {
    return new IntegrationChainResult(Collections.emptyList(), warning);
  }

  public boolean hasWarning() {
    return warning != null;
  }
}
