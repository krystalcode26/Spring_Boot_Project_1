package net.javaguides.ems.config;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.entity.Employee;
import net.javaguides.ems.entity.Student;
import net.javaguides.ems.repository.EmployeeRepository;
import net.javaguides.ems.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthSeedProperties.class)
@Profile("auth")
public class DataInitializer {

  private final AuthSeedProperties seedProperties;
  private final StudentRepository studentRepository;
  private final EmployeeRepository employeeRepository;
  private final PasswordEncoder passwordEncoder;

  @Bean
  CommandLineRunner seedAuthAccounts() {
    return args -> {
      if (studentRepository.findByEmail(seedProperties.userEmail()).isEmpty()) {
        studentRepository.save(new Student(
            null,
            "Regular",
            "User",
            seedProperties.userEmail(),
            passwordEncoder.encode(seedProperties.userCredential()),
            seedProperties.userRole()));
      }

      if (employeeRepository.findByEmail(seedProperties.adminEmail()).isEmpty()) {
        Employee employee = new Employee();
        employee.setFirstName("System");
        employee.setLastName("Admin");
        employee.setEmpName("System Admin");
        employee.setAge(30);
        employee.setSalary(BigDecimal.ZERO);
        employee.setEmail(seedProperties.adminEmail());
        employee.setPassword(passwordEncoder.encode(seedProperties.adminCredential()));
        employee.setRole(seedProperties.adminRole());
        employeeRepository.save(employee);
      }
    };
  }
}
