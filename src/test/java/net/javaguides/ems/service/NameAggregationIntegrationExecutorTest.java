package net.javaguides.ems.service;

import net.javaguides.ems.client.DownstreamModuleClient;
import net.javaguides.ems.client.UpstreamModuleClient;
import net.javaguides.ems.dto.IntegrationChainResult;
import net.javaguides.ems.dto.NameAggregationRequest;
import net.javaguides.ems.dto.NameAggregationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NameAggregationIntegrationExecutorTest {

  @Mock
  private UpstreamModuleClient upstreamModuleClient;

  @Mock
  private DownstreamModuleClient downstreamModuleClient;

  @InjectMocks
  private NameAggregationIntegrationExecutor executor;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(executor, "asyncTimeoutSeconds", 10L);
    ReflectionTestUtils.setField(executor, "downstreamEnabled", true);
  }

  @Test
  void executeChain_returnsDownstreamNamesWhenCallSucceeds() {
    NameAggregationRequest request = new NameAggregationRequest(List.of("Alice"));
    NameAggregationResponse downstreamResponse = new NameAggregationResponse();
    downstreamResponse.setName(List.of("Bob"));

    when(upstreamModuleClient.notifyAsync(any()))
        .thenReturn(CompletableFuture.completedFuture(null));
    when(downstreamModuleClient.fetchNamesAsync(any()))
        .thenReturn(CompletableFuture.completedFuture(downstreamResponse));

    IntegrationChainResult result = executor.executeChain(request, Set.of("Alice", "Test"));

    assertThat(result.getDownstreamNames()).containsExactly("Bob");
    assertThat(result.getWarning()).isNull();
  }

  @Test
  void executeChain_skipsDownstreamWhenDisabled() {
    ReflectionTestUtils.setField(executor, "downstreamEnabled", false);
    when(upstreamModuleClient.notifyAsync(any()))
        .thenReturn(CompletableFuture.completedFuture(null));

    IntegrationChainResult result = executor.executeChain(
        new NameAggregationRequest(List.of("Alice")),
        Set.of("Alice", "Test"));

    assertThat(result.getDownstreamNames()).isEmpty();
    assertThat(result.getWarning()).isNull();
  }
}
