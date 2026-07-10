package net.javaguides.ems.mapper;

import net.javaguides.ems.dto.EmployeeDto;
import net.javaguides.ems.entity.Department;
import net.javaguides.ems.entity.Employee;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeMapper {

  public static EmployeeDto mapToEmployeeDto(Employee employee) {
    List<Integer> departmentIds = employee.getDepartments().stream()
        .map(Department::getDeptId)
        .collect(Collectors.toList());

    return new EmployeeDto(
        employee.getEmpId(),
        resolveFirstName(employee),
        resolveLastName(employee),
        employee.getEmail(),
        departmentIds,
        employee.getAge(),
        employee.getSalary()
    );
  }

  public static Employee mapToEmployee(EmployeeDto employeeDto) {
    Employee employee = new Employee();
    employee.setEmpId(employeeDto.getEmpId());
    applyNameFields(employee, employeeDto.getFirstName(), employeeDto.getLastName());
    employee.setEmail(employeeDto.getEmail());
    employee.setAge(employeeDto.getAge());
    employee.setSalary(employeeDto.getSalary());
    employee.setDepartments(new HashSet<>());
    return employee;
  }

  public static void applyNameFields(Employee employee, String firstName, String lastName) {
    employee.setFirstName(firstName);
    employee.setLastName(lastName);
    employee.setEmpName(buildFullName(firstName, lastName));
  }

  public static String buildFullName(String firstName, String lastName) {
    return (firstName + " " + lastName).trim();
  }

  public static String displayName(Employee employee) {
    if (employee.getFirstName() != null || employee.getLastName() != null) {
      return buildFullName(
          employee.getFirstName() != null ? employee.getFirstName() : "",
          employee.getLastName() != null ? employee.getLastName() : "");
    }
    return employee.getEmpName();
  }

  private static String resolveFirstName(Employee employee) {
    if (employee.getFirstName() != null) {
      return employee.getFirstName();
    }
    if (employee.getEmpName() == null) {
      return "";
    }
    String[] parts = employee.getEmpName().trim().split("\\s+", 2);
    return parts[0];
  }

  private static String resolveLastName(Employee employee) {
    if (employee.getLastName() != null) {
      return employee.getLastName();
    }
    if (employee.getEmpName() == null) {
      return "";
    }
    String[] parts = employee.getEmpName().trim().split("\\s+", 2);
    return parts.length > 1 ? parts[1] : "";
  }
}
