package net.javaguides.ems.service.impl;

import lombok.AllArgsConstructor;
import net.javaguides.ems.dto.EmployeeDto;
import net.javaguides.ems.entity.Employee;
import net.javaguides.ems.exception.ResourceNotFoundException;
import net.javaguides.ems.mapper.EmployeeMapper;
import net.javaguides.ems.repository.EmployeeRepository;
import net.javaguides.ems.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//Spring container to create SpringBean for the class
@Service
//use lumo annotation so don't need to create manually - dependency injection to inject dependency
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  //employeeRepository is a dependency
  private final EmployeeRepository employeeRepository;
  @Override
  public EmployeeDto createEmployee(EmployeeDto employeeDto) {

    //store employee into database
    Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
    Employee savedEmployee = employeeRepository.save(employee);
    return EmployeeMapper.mapToEmployeeDto(savedEmployee);
  }

  //throw custom exception here in case there's no id here
  @Override
  public EmployeeDto getEmployeeById(Long employeeId) {
    Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("employee is not exist with given id." + employeeId));
    return EmployeeMapper.mapToEmployeeDto(employee);
  }


  //convert list of employee jpa entities(List<Employee> employees ) into a list of employee dtos(List<EmployeeDto>)
  @Override
  public List<EmployeeDto> getAllEmployees() {
    List<Employee> employees = employeeRepository.findAll();

    return employees.stream()
            .map((employee) -> EmployeeMapper.mapToEmployeeDto(employee))
            .collect(Collectors.toList());
  }

  @Override
  public EmployeeDto updateEmployee(Long employeeId, EmployeeDto updateEmployee) {

    Employee employee = employeeRepository.findById(employeeId).orElseThrow(
            ()-> new ResourceNotFoundException("Employee is not exists with given id: " + employeeId)
    );

    employee.setFirstName(updateEmployee.getFirstName());
    employee.setLastName(updateEmployee.getLastName());
    employee.setEmail(updateEmployee.getEmail());

    Employee updatedEmployee = employeeRepository.save(employee);

    return EmployeeMapper.mapToEmployeeDto(updatedEmployee);
  }

  //throw exception first for not found id
  @Override
  public void deleteEmployee(Long employeeId) {
    employeeRepository.findById(employeeId).orElseThrow(
            () -> new ResourceNotFoundException("Employee is not exists with the given id: " + employeeId)
    );

    employeeRepository.deleteById(employeeId);
  }


}
