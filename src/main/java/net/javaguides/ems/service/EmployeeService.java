package net.javaguides.ems.service;

import net.javaguides.ems.dto.EmployeeDto;
import net.javaguides.ems.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

  EmployeeDto createEmployee(EmployeeDto employeeDto);

  EmployeeDto getEmployeeById(Long empId);

  List<EmployeeDto> getAllEmployees();

  PagedResponse<EmployeeDto> getEmployeesPaged(String query, Pageable pageable);

  EmployeeDto updateEmployee(Long empId, EmployeeDto updatedEmployee);

  void deleteEmployee(Long empId);
}
