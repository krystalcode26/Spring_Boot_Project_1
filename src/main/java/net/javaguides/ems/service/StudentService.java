package net.javaguides.ems.service;

import net.javaguides.ems.dto.StudentDto;
//import org.springframework.stereotype.Service;

import java.util.List;


public interface StudentService {

  StudentDto createStudent(StudentDto studentDto);

  StudentDto getStudentById(Long id);

  List<StudentDto> getAllStudents();

  StudentDto updateStudent(Long studentId, StudentDto updatedStudent);

  void deleteStudent(Long studentId);
}
