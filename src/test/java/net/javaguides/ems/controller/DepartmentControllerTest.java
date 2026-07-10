package net.javaguides.ems.controller;

import net.javaguides.ems.dto.DepartmentDto;
import net.javaguides.ems.service.DepartmentService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

  @Mock
  private DepartmentService departmentService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new DepartmentController(departmentService)).build();
  }

  @Test
  void createDepartment_returns201() throws Exception {
    when(departmentService.createDepartment(any())).thenReturn(new DepartmentDto(1, "Engineering"));

    mockMvc.perform(post("/api/departments")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"deptName":"Engineering"}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.deptName").value("Engineering"));
  }

  @Test
  void getDepartmentById_returns200() throws Exception {
    when(departmentService.getDepartmentById(1)).thenReturn(new DepartmentDto(1, "Engineering"));

    mockMvc.perform(get("/api/departments/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.deptId").value(1));
  }

  @Test
  void getAllDepartments_returns200() throws Exception {
    when(departmentService.getAllDepartments()).thenReturn(List.of(new DepartmentDto(1, "Engineering")));

    mockMvc.perform(get("/api/departments"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].deptName").value("Engineering"));
  }

  @Test
  void updateDepartment_returns200() throws Exception {
    when(departmentService.updateDepartment(any(), any())).thenReturn(new DepartmentDto(1, "HR"));

    mockMvc.perform(put("/api/departments/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"deptName":"HR"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.deptName").value("HR"));
  }

  @Test
  void deleteDepartment_returns200() throws Exception {
    mockMvc.perform(delete("/api/departments/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value("Department deleted successfully"));
  }
}
