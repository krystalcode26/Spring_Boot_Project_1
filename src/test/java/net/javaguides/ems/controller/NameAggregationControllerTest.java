package net.javaguides.ems.controller;

import net.javaguides.ems.dto.NameAggregationResponse;
import net.javaguides.ems.service.NameAggregationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NameAggregationControllerTest {

  @Mock
  private NameAggregationService nameAggregationService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new NameAggregationController(nameAggregationService)).build();
  }

  @Test
  void aggregate_returns200WithWarningWhenDownstreamFails() throws Exception {
    NameAggregationResponse response = new NameAggregationResponse();
    response.setName(List.of("Alice", "Test"));
    response.setWarning("Downstream unavailable");

    when(nameAggregationService.aggregate(any())).thenReturn(response);

    mockMvc.perform(post("/name/aggregation")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":[\"Alice\"]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name[0]").value("Alice"))
        .andExpect(jsonPath("$.name[1]").value("Test"))
        .andExpect(jsonPath("$.warning").value("Downstream unavailable"));
  }
}
