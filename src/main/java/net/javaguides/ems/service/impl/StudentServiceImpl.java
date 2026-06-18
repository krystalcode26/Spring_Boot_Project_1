package net.javaguides.ems.service.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.javaguides.ems.dto.StudentDto;
import net.javaguides.ems.entity.Student;
import net.javaguides.ems.exception.ResourceNotFoundException;
import net.javaguides.ems.mapper.StudentMapper;
import net.javaguides.ems.repository.StudentRepository;
//import net.javaguides.ems.service.StudentNotificationService;
import net.javaguides.ems.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//These four annotation used for mark class as Spring bBean.
//@Controller
//@RestController
//@Repository
//@AllArgsConstructor and @RequiredArgsConstructor are both Lombok annotations that generate a constructor 
//@RequiredArgsConstructor: Generates a constructor only for final fields (and fields marked @NonNull).
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

  private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

  private final StudentRepository studentRepository;
  private final MeterRegistry meterRegistry;

  private Counter createCounter;
  private Counter deleteCounter;

  @PostConstruct
  public void initMetrics() {
    createCounter = Counter.builder("student.created.total")
        .description("Total students created")
        .register(meterRegistry);
    deleteCounter = Counter.builder("student.deleted.total")
        .description("Total students deleted")
        .register(meterRegistry);
    Gauge.builder("student.count", studentRepository, StudentRepository::count)
        .description("Current number of students in database")
        .register(meterRegistry);
  }

  @Override
  @CacheEvict(value = "students", allEntries = true)
  public StudentDto createStudent(StudentDto studentDto) {
    log.info("Creating student with email={}", studentDto.getEmail());
    Student student = StudentMapper.mapToStudent(studentDto);
    student.setId(null);
    Student savedStudent = studentRepository.save(student);
    createCounter.increment();
    return StudentMapper.mapToStudentDto(savedStudent);
  }

  @Override
  @Cacheable(value = "students", key = "#studentId")
  public StudentDto getStudentById(Long studentId) {
    log.info("Fetching student from database, id={}", studentId);
    Student student = studentRepository.findById(studentId)
        .orElseThrow(() ->
            new ResourceNotFoundException("Student does not exist with given id: " + studentId));
    return StudentMapper.mapToStudentDto(student);
  }

  @Override
  @Cacheable(value = "students", key = "'all'")
  public List<StudentDto> getAllStudents() {
    log.info("Fetching all students from database");
    List<Student> students = studentRepository.findAll();

    return students.stream()
        .map(StudentMapper::mapToStudentDto)
        .collect(Collectors.toList());
  }

  @Override
  @CacheEvict(value = "students", allEntries = true)
  public StudentDto updateStudent(Long studentId, StudentDto updatedStudent) {
    log.info("Updating student id={}", studentId);
    Student student = studentRepository.findById(studentId).orElseThrow(
        () -> new ResourceNotFoundException("Student does not exist with given id: " + studentId)
    );

    student.setFirstName(updatedStudent.getFirstName());
    student.setLastName(updatedStudent.getLastName());
    student.setEmail(updatedStudent.getEmail());

    Student savedStudent = studentRepository.save(student);
    return StudentMapper.mapToStudentDto(savedStudent);
  }

  @Override
  @CacheEvict(value = "students", allEntries = true)
  public void deleteStudent(Long studentId) {
    log.info("Deleting student id={}", studentId);
    studentRepository.findById(studentId).orElseThrow(
        () -> new ResourceNotFoundException("Student does not exist with given id: " + studentId)
    );

    studentRepository.deleteById(studentId);
    deleteCounter.increment();
  }
}
