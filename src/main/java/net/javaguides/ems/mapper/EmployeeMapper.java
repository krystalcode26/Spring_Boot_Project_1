package net.javaguides.ems.mapper;

import net.javaguides.ems.dto.EmployeeDto;
import net.javaguides.ems.entity.Employee;

import java.security.cert.Extension;

//keep the common logic rather rewrite all things repeatedly
public class EmployeeMapper {
  public static EmployeeDto mapToEmployeeDto(Employee employee){
    return new EmployeeDto(
            employee.getId(),
            employee.getFirstName(),
            employee.getLastName(),
            employee.getEmail()
    );
  }

  public static Employee mapToEmployee(EmployeeDto employeeDto){
    return new Employee(
            employeeDto.getId(),
            employeeDto.getFirstName(),
            employeeDto.getLastName(),
            employeeDto.getEmail()
    );
  }


}
