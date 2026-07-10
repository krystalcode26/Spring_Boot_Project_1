package net.javaguides.ems.security;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.entity.Employee;
import net.javaguides.ems.entity.Student;
import net.javaguides.ems.mapper.EmployeeMapper;
import net.javaguides.ems.repository.EmployeeRepository;
import net.javaguides.ems.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthAccountService {

  private final StudentRepository studentRepository;
  private final EmployeeRepository employeeRepository;

  public Optional<AuthAccount> findByEmail(String email) {
    return studentRepository.findByEmail(email)
        .map(this::fromStudent)
        .or(() -> employeeRepository.findByEmail(email).map(this::fromEmployee));
  }

  private AuthAccount fromStudent(Student student) {
    String name = student.getFirstName() + " " + student.getLastName();
    return new AuthAccount(student.getEmail(), name.trim(), student.getRole(), student.getPassword());
  }

  private AuthAccount fromEmployee(Employee employee) {
    return new AuthAccount(
        employee.getEmail(),
        EmployeeMapper.displayName(employee),
        employee.getRole(),
        employee.getPassword());
  }
}
