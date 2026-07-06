package net.javaguides.ems.testutil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class RestClientTestSupport {

  private RestClientTestSupport() {}

  @SuppressWarnings("unchecked")
  public static void stubPostBody(RestClient restClient, Object responseBody) {
    RestClient.ResponseSpec responseSpec = stubPostChain(restClient);
    when(responseSpec.body(any(Class.class))).thenReturn(responseBody);
  }

  @SuppressWarnings("unchecked")
  public static void stubPostFailure(RestClient restClient, RuntimeException exception) {
    RestClient.ResponseSpec responseSpec = stubPostChain(restClient);
    when(responseSpec.body(any(Class.class))).thenThrow(exception);
  }

  public static void stubPostBodiless(RestClient restClient) {
    RestClient.ResponseSpec responseSpec = stubPostChain(restClient);
    when(responseSpec.toBodilessEntity()).thenReturn(ResponseEntity.ok().build());
  }

  private static RestClient.ResponseSpec stubPostChain(RestClient restClient) {
    RestClient.RequestBodyUriSpec uriSpec = mock(RestClient.RequestBodyUriSpec.class);
    RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
    RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

    when(restClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri(anyString())).thenReturn(bodySpec);
    when(bodySpec.body(any(Object.class))).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(responseSpec);
    return responseSpec;
  }
}
