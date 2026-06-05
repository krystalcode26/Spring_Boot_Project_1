package net.javaguides.ems.service;

import net.javaguides.ems.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {
  // defines methods
  EmployeeDto createEmployee(EmployeeDto employeeDto);

  EmployeeDto getEmployeeById(Long id);

  List<EmployeeDto> getAllEmployees();

  EmployeeDto updateEmployee(Long employeeId, EmployeeDto updateEmployee);

  void deleteEmployee(Long employeeId);
}
