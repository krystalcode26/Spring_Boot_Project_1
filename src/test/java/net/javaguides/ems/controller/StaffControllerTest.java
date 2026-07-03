package net.javaguides.ems.controller;

import net.javaguides.ems.dto.StaffDto;
import net.javaguides.ems.service.StaffService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StaffControllerTest {

  @Mock
  private StaffService staffService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new StaffController(staffService)).build();
  }

  @Test
  void createStaff_returns201() throws Exception {
    when(staffService.createStaff(any()))
        .thenReturn(new StaffDto(1L, "Jane", "Doe", "jane@example.com"));

    mockMvc.perform(post("/api/staffs")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"firstName":"Jane","lastName":"Doe","email":"jane@example.com"}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("jane@example.com"));
  }

  @Test
  void getStaffById_returns200() throws Exception {
    when(staffService.getStaffById(1L))
        .thenReturn(new StaffDto(1L, "Jane", "Doe", "jane@example.com"));

    mockMvc.perform(get("/api/staffs/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Jane"));
  }

  @Test
  void getAllStaff_returns200() throws Exception {
    when(staffService.getAllStaff()).thenReturn(List.of(
        new StaffDto(1L, "Jane", "Doe", "jane@example.com")));

    mockMvc.perform(get("/api/staffs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].lastName").value("Doe"));
  }

  @Test
  void deleteStaff_returns200() throws Exception {
    mockMvc.perform(delete("/api/staffs/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value("Staff deleted successfully"));

    verify(staffService).deleteStaff(1L);
  }
}
