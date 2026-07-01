package net.javaguides.ems.mapper;

import net.javaguides.ems.dto.EmployeeDto;
import net.javaguides.ems.entity.Employee;

public class EmployeeMapper {

  public static EmployeeDto mapToEmployeeDto(Employee employee) {
    return new EmployeeDto(
        employee.getEmpId(),
        employee.getEmpName(),
        employee.getDeptId(),
        employee.getAge(),
        employee.getSalary()
    );
  }

  public static Employee mapToEmployee(EmployeeDto employeeDto) {
    return new Employee(
        employeeDto.getEmpId(),
        employeeDto.getEmpName(),
        employeeDto.getDeptId(),
        employeeDto.getAge(),
        employeeDto.getSalary()
    );
  }
}
