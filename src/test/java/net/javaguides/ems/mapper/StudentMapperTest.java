package net.javaguides.ems.mapper;

import net.javaguides.ems.dto.StudentDto;
import net.javaguides.ems.entity.Student;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudentMapperTest {

  @Test
  void mapToStudentDto_mapsAllFields() {
    Student student = new Student(1L, "Alice", "Smith", "alice@example.com", null, null);

    StudentDto dto = StudentMapper.mapToStudentDto(student);

    assertThat(dto.getId()).isEqualTo(1L);
    assertThat(dto.getFirstName()).isEqualTo("Alice");
    assertThat(dto.getLastName()).isEqualTo("Smith");
    assertThat(dto.getEmail()).isEqualTo("alice@example.com");
  }

  @Test
  void mapToStudent_mapsAllFields() {
    StudentDto dto = new StudentDto(2L, "Bob", "Lee", "bob@example.com");

    Student student = StudentMapper.mapToStudent(dto);

    assertThat(student.getId()).isEqualTo(2L);
    assertThat(student.getFirstName()).isEqualTo("Bob");
    assertThat(student.getEmail()).isEqualTo("bob@example.com");
  }
}
