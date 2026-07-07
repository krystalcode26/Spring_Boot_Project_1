package net.javaguides.ems.config;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.entity.Employee;
import net.javaguides.ems.entity.Student;
import net.javaguides.ems.repository.EmployeeRepository;
import net.javaguides.ems.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "security.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer {

  private static final String USER_EMAIL = "user@ems.com";
  private static final String USER_PASSWORD = "user123";
  private static final String USER_ROLE = "ROLE_USER";

  private static final String ADMIN_EMAIL = "admin@ems.com";
  private static final String ADMIN_PASSWORD = "admin123";
  private static final String ADMIN_ROLE = "ROLE_ADMIN";

  private final StudentRepository studentRepository;
  private final EmployeeRepository employeeRepository;
  private final PasswordEncoder passwordEncoder;

  @Bean
  CommandLineRunner seedAuthAccounts() {
    return args -> {
      if (studentRepository.findByEmail(USER_EMAIL).isEmpty()) {
        studentRepository.save(new Student(
            null,
            "Regular",
            "User",
            USER_EMAIL,
            passwordEncoder.encode(USER_PASSWORD),
            USER_ROLE));
      }

      if (employeeRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {
        Employee employee = new Employee();
        employee.setEmpName("Admin");
        employee.setEmail(ADMIN_EMAIL);
        employee.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        employee.setRole(ADMIN_ROLE);
        employeeRepository.save(employee);
      }
    };
  }
}
