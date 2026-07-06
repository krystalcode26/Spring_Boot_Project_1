package net.javaguides.ems.repository;

import net.javaguides.ems.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class EmployeeRepositoryTest {

  @Autowired
  private EmployeeRepository employeeRepository;

  @Test
  void findById_returnsEmployee_whenExists() {
    Employee employee = new Employee();
    employee.setEmpName("Jane");
    employee.setAge(30);
    employee.setSalary(new BigDecimal("75000"));
    Employee saved = employeeRepository.save(employee);

    Optional<Employee> found = employeeRepository.findById(saved.getEmpId());

    assertThat(found).isPresent();
    assertThat(found.get().getEmpName()).isEqualTo("Jane");
  }

  @Test
  void save_persistsNewEmployee() {
    Employee employee = new Employee();
    employee.setEmpName("John");
    employee.setAge(28);
    employee.setSalary(new BigDecimal("60000"));

    Employee saved = employeeRepository.save(employee);

    assertThat(saved.getEmpId()).isNotNull();
    assertThat(employeeRepository.findById(saved.getEmpId())).isPresent();
  }
}
