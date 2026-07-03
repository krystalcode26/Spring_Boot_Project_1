package net.javaguides.ems.controller;

import net.javaguides.ems.dto.StudentDto;
import net.javaguides.ems.service.StudentService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

  @Mock
  private StudentService studentService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new StudentController(studentService)).build();
  }

  @Test
  void createStudent_returns201() throws Exception {
    StudentDto saved = new StudentDto(1L, "Alice", "Smith", "alice@example.com");
    when(studentService.createStudent(any())).thenReturn(saved);

    mockMvc.perform(post("/api/students")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"firstName":"Alice","lastName":"Smith","email":"alice@example.com"}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("alice@example.com"));
  }

  @Test
  void getStudentById_returns200() throws Exception {
    when(studentService.getStudentById(1L))
        .thenReturn(new StudentDto(1L, "Alice", "Smith", "alice@example.com"));

    mockMvc.perform(get("/api/students/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Alice"));
  }

  @Test
  void getAllStudents_returns200() throws Exception {
    when(studentService.getAllStudents()).thenReturn(List.of(
        new StudentDto(1L, "Alice", "Smith", "alice@example.com")));

    mockMvc.perform(get("/api/students"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].lastName").value("Smith"));
  }

  @Test
  void updateStudent_returns200() throws Exception {
    when(studentService.updateStudent(eq(1L), any()))
        .thenReturn(new StudentDto(1L, "Alice", "Updated", "alice@example.com"));

    mockMvc.perform(put("/api/students/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"firstName":"Alice","lastName":"Updated","email":"alice@example.com"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lastName").value("Updated"));
  }

  @Test
  void deleteStudent_returns200() throws Exception {
    mockMvc.perform(delete("/api/students/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value("Student deleted successfully"));

    verify(studentService).deleteStudent(1L);
  }
}
