package net.javaguides.ems.repository;

import net.javaguides.ems.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class StudentRepositoryTest {

  @Autowired
  private StudentRepository studentRepository;

  @Test
  void findById_returnsStudent_whenExists() {
    Student saved = studentRepository.save(new Student(null, "Jane", "Smith", "jane@repo.com"));

    Optional<Student> found = studentRepository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("jane@repo.com");
  }

  @Test
  void save_throwsDataIntegrityViolation_onDuplicateEmail() {
    studentRepository.saveAndFlush(new Student(null, "John", "Doe", "dup@repo.com"));
    Student duplicate = new Student(null, "Jane", "Smith", "dup@repo.com");

    assertThatThrownBy(() -> studentRepository.saveAndFlush(duplicate))
        .isInstanceOf(DataIntegrityViolationException.class);
  }
}
