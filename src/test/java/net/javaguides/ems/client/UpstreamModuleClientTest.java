package net.javaguides.ems.client;

import net.javaguides.ems.dto.NameAggregationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UpstreamModuleClientTest {

  @Mock
  private RestClient restClient;

  private UpstreamModuleClient upstreamModuleClient;

  @BeforeEach
  void setUp() {
    upstreamModuleClient = new UpstreamModuleClient(restClient);
    ReflectionTestUtils.setField(upstreamModuleClient, "enabled", false);
    ReflectionTestUtils.setField(upstreamModuleClient, "upstreamUrl", "http://upstream");
    ReflectionTestUtils.setField(upstreamModuleClient, "upstreamPath", "/name/notify");
  }

  @Test
  void notifyAsync_skipsCallWhenDisabled() throws Exception {
    CompletableFuture<Void> future = upstreamModuleClient.notifyAsync(
        new NameAggregationRequest(List.of("Alice")));

    assertThat(future.get()).isNull();
  }

  @Test
  void notifyAsync_callsUpstreamWhenEnabled() throws Exception {
    ReflectionTestUtils.setField(upstreamModuleClient, "enabled", true);
    net.javaguides.ems.testutil.RestClientTestSupport.stubPostBodiless(restClient);

    CompletableFuture<Void> future = upstreamModuleClient.notifyAsync(
        new NameAggregationRequest(List.of("Alice", "Bob")));

    assertThat(future.get()).isNull();
  }
}
