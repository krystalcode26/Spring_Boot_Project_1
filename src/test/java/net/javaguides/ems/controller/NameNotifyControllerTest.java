package net.javaguides.ems.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NameNotifyControllerTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new NameNotifyController()).build();
  }

  @Test
  void notify_returnsReceivedStatus() throws Exception {
    mockMvc.perform(post("/name/notify")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":[\"Alice\",\"Test\"]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("received"))
        .andExpect(jsonPath("$.name[0]").value("Alice"));
  }
}
