package net.javaguides.ems.mapper;

import net.javaguides.ems.dto.EmployeeDto;
import net.javaguides.ems.entity.Department;
import net.javaguides.ems.entity.Employee;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeMapperTest {

  @Test
  void mapToEmployeeDto_mapsAllFields() {
    Department department = new Department();
    department.setDeptId(1);
    department.setDeptName("Engineering");

    Employee employee = new Employee();
    employee.setEmpId(10L);
    employee.setFirstName("Alice");
    employee.setLastName("Smith");
    employee.setEmpName("Alice Smith");
    employee.setEmail("alice@example.com");
    employee.setAge(30);
    employee.setSalary(new BigDecimal("75000.00"));
    employee.setDepartments(Set.of(department));

    EmployeeDto dto = EmployeeMapper.mapToEmployeeDto(employee);

    assertThat(dto.getEmpId()).isEqualTo(10L);
    assertThat(dto.getFirstName()).isEqualTo("Alice");
    assertThat(dto.getLastName()).isEqualTo("Smith");
    assertThat(dto.getEmail()).isEqualTo("alice@example.com");
    assertThat(dto.getDepartmentIds()).containsExactly(1);
    assertThat(dto.getAge()).isEqualTo(30);
    assertThat(dto.getSalary()).isEqualByComparingTo("75000.00");
  }

  @Test
  void mapToEmployeeDto_splitsLegacyEmpName() {
    Employee employee = new Employee();
    employee.setEmpId(11L);
    employee.setEmpName("Legacy User");

    EmployeeDto dto = EmployeeMapper.mapToEmployeeDto(employee);

    assertThat(dto.getFirstName()).isEqualTo("Legacy");
    assertThat(dto.getLastName()).isEqualTo("User");
  }

  @Test
  void mapToEmployee_mapsAllFields() {
    EmployeeDto dto = new EmployeeDto(
        20L,
        "Bob",
        "Jones",
        "bob@example.com",
        List.of(2, 3),
        28,
        new BigDecimal("65000.00")
    );

    Employee employee = EmployeeMapper.mapToEmployee(dto);

    assertThat(employee.getEmpId()).isEqualTo(20L);
    assertThat(employee.getFirstName()).isEqualTo("Bob");
    assertThat(employee.getLastName()).isEqualTo("Jones");
    assertThat(employee.getEmpName()).isEqualTo("Bob Jones");
    assertThat(employee.getEmail()).isEqualTo("bob@example.com");
    assertThat(employee.getAge()).isEqualTo(28);
    assertThat(employee.getSalary()).isEqualByComparingTo("65000.00");
    assertThat(employee.getDepartments()).isEmpty();
  }
}
