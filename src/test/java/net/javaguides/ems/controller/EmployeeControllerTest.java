package net.javaguides.ems.controller;

import net.javaguides.ems.dto.EmployeeDto;
import net.javaguides.ems.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

  @Mock
  private EmployeeService employeeService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new EmployeeController(employeeService)).build();
  }

  @Test
  void createEmployee_returns201() throws Exception {
    EmployeeDto saved = new EmployeeDto(1L, "Alice", List.of(1), 30, new BigDecimal("75000"));
    when(employeeService.createEmployee(any())).thenReturn(saved);

    mockMvc.perform(post("/api/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"empName":"Alice","departmentIds":[1],"age":30,"salary":75000}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.empName").value("Alice"));
  }

  @Test
  void getEmployeeById_returns200() throws Exception {
    when(employeeService.getEmployeeById(1L))
        .thenReturn(new EmployeeDto(1L, "Alice", List.of(1), 30, new BigDecimal("75000")));

    mockMvc.perform(get("/api/employees/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.empName").value("Alice"));
  }

  @Test
  void getAllEmployees_returns200() throws Exception {
    when(employeeService.getAllEmployees()).thenReturn(List.of(
        new EmployeeDto(1L, "Alice", List.of(1), 30, new BigDecimal("75000"))));

    mockMvc.perform(get("/api/employees"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].empName").value("Alice"));
  }

  @Test
  void deleteEmployee_returns200() throws Exception {
    mockMvc.perform(delete("/api/employees/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value("Employee deleted successfully"));

    verify(employeeService).deleteEmployee(1L);
  }

  @Test
  void updateEmployee_returns200() throws Exception {
    when(employeeService.updateEmployee(any(), any()))
        .thenReturn(new EmployeeDto(1L, "Alice Updated", List.of(1), 31, new BigDecimal("80000")));

    mockMvc.perform(put("/api/employees/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"empName":"Alice Updated","departmentIds":[1],"age":31,"salary":80000}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.empName").value("Alice Updated"));
  }
}
