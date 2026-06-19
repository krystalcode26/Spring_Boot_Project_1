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
        employee.getEmpName(),
        departmentIds,
        employee.getAge(),
        employee.getSalary()
    );
  }

  public static Employee mapToEmployee(EmployeeDto employeeDto) {
    Employee employee = new Employee();
    employee.setEmpId(employeeDto.getEmpId());
    employee.setEmpName(employeeDto.getEmpName());
    employee.setAge(employeeDto.getAge());
    employee.setSalary(employeeDto.getSalary());
    employee.setDepartments(new HashSet<>());
    return employee;
  }
}
