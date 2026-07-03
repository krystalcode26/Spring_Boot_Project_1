package net.javaguides.ems.controller;

import net.javaguides.ems.service.DemoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DemoControllerTest {

  @Mock
  private DemoService demoService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new DemoController(demoService)).build();
  }

  @Test
  void problemA_returnsServiceResponse() throws Exception {
    when(demoService.triggerProblemA()).thenReturn("Problem A done");

    mockMvc.perform(get("/api/demo/problem-a"))
        .andExpect(status().isOk())
        .andExpect(content().string("Problem A done"));
  }

  @Test
  void problemB_returnsServiceResponse() throws Exception {
    when(demoService.triggerProblemB()).thenReturn("Problem B done");

    mockMvc.perform(get("/api/demo/problem-b"))
        .andExpect(status().isOk())
        .andExpect(content().string("Problem B done"));
  }
}
