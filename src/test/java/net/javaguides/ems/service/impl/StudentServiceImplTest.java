package net.javaguides.ems.service.impl;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import net.javaguides.ems.dto.StudentDto;
import net.javaguides.ems.entity.Student;
import net.javaguides.ems.exception.ResourceNotFoundException;
import net.javaguides.ems.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

  @Mock
  private StudentRepository studentRepository;

  private StudentServiceImpl studentService;

  @BeforeEach
  void setUp() {
    studentService = new StudentServiceImpl(studentRepository, new SimpleMeterRegistry());
    studentService.initMetrics();
  }

  @Test
  void createStudent_savesAndReturnsDto() {
    StudentDto request = new StudentDto(null, "Alice", "Smith", "alice@example.com");
    Student saved = new Student(1L, "Alice", "Smith", "alice@example.com", null, null);
    when(studentRepository.save(any(Student.class))).thenReturn(saved);

    StudentDto response = studentService.createStudent(request);

    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getEmail()).isEqualTo("alice@example.com");
    verify(studentRepository).save(any(Student.class));
  }

  @Test
  void getStudentById_returnsDtoWhenFound() {
    Student student = new Student(1L, "Alice", "Smith", "alice@example.com", null, null);
    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

    StudentDto dto = studentService.getStudentById(1L);

    assertThat(dto.getFirstName()).isEqualTo("Alice");
    verify(studentRepository).findById(1L);
  }

  @Test
  void getStudentById_throwsWhenNotFound() {
    when(studentRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> studentService.getStudentById(99L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void getAllStudents_returnsMappedList() {
    when(studentRepository.findAll()).thenReturn(List.of(
        new Student(1L, "Alice", "Smith", "alice@example.com", null, null)));

    List<StudentDto> students = studentService.getAllStudents();

    assertThat(students).hasSize(1);
    assertThat(students.get(0).getEmail()).isEqualTo("alice@example.com");
  }

  @Test
  void deleteStudent_deletesWhenFound() {
    when(studentRepository.findById(1L)).thenReturn(Optional.of(
        new Student(1L, "Alice", "Smith", "alice@example.com", null, null)));

    studentService.deleteStudent(1L);

    verify(studentRepository).deleteById(1L);
  }

  @Test
  void updateStudent_updatesAndReturnsDto() {
    Student existing = new Student(1L, "Alice", "Smith", "alice@example.com", null, null);
    StudentDto update = new StudentDto(null, "Alice", "Jones", "alice.jones@example.com");
    Student saved = new Student(1L, "Alice", "Jones", "alice.jones@example.com", null, null);
    when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(studentRepository.save(any(Student.class))).thenReturn(saved);

    StudentDto response = studentService.updateStudent(1L, update);

    assertThat(response.getLastName()).isEqualTo("Jones");
    assertThat(response.getEmail()).isEqualTo("alice.jones@example.com");
  }

  @Test
  void updateStudent_throwsWhenNotFound() {
    when(studentRepository.findById(99L)).thenReturn(Optional.empty());
    StudentDto update = new StudentDto(null, "A", "B", "a@b.com");

    assertThatThrownBy(() -> studentService.updateStudent(99L, update))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void deleteStudent_throwsWhenNotFound() {
    when(studentRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> studentService.deleteStudent(99L))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
