package net.javaguides.ems.service.impl;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.dto.EmployeeDto;
import net.javaguides.ems.entity.Department;
import net.javaguides.ems.entity.Employee;
import net.javaguides.ems.exception.ResourceNotFoundException;
import net.javaguides.ems.mapper.EmployeeMapper;
import net.javaguides.ems.repository.DepartmentRepository;
import net.javaguides.ems.repository.EmployeeRepository;
import net.javaguides.ems.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;

  @Override
  public EmployeeDto createEmployee(EmployeeDto employeeDto) {
    log.info("Creating employee with name={}", employeeDto.getEmpName());
    Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
    employee.setEmpId(null);
    employee.setDepartments(resolveDepartments(employeeDto.getDepartmentIds()));
    Employee savedEmployee = employeeRepository.save(employee);
    return EmployeeMapper.mapToEmployeeDto(savedEmployee);
  }

  @Override
  public EmployeeDto getEmployeeById(Long empId) {
    log.info("Fetching employee from database, id={}", empId);
    Employee employee = employeeRepository.findById(empId)
        .orElseThrow(() ->
            new ResourceNotFoundException("Employee does not exist with given id: " + empId));
    return EmployeeMapper.mapToEmployeeDto(employee);
  }

  @Override
  public List<EmployeeDto> getAllEmployees() {
    log.info("Fetching all employees from database");
    return employeeRepository.findAll().stream()
        .map(EmployeeMapper::mapToEmployeeDto)
        .collect(Collectors.toList());
  }

  @Override
  public EmployeeDto updateEmployee(Long empId, EmployeeDto updatedEmployee) {
    log.info("Updating employee id={}", empId);
    Employee employee = employeeRepository.findById(empId).orElseThrow(
        () -> new ResourceNotFoundException("Employee does not exist with given id: " + empId)
    );

    employee.setEmpName(updatedEmployee.getEmpName());
    employee.setAge(updatedEmployee.getAge());
    employee.setSalary(updatedEmployee.getSalary());
    employee.setDepartments(resolveDepartments(updatedEmployee.getDepartmentIds()));

    Employee savedEmployee = employeeRepository.save(employee);
    return EmployeeMapper.mapToEmployeeDto(savedEmployee);
  }

  @Override
  public void deleteEmployee(Long empId) {
    log.info("Deleting employee id={}", empId);
    employeeRepository.findById(empId).orElseThrow(
        () -> new ResourceNotFoundException("Employee does not exist with given id: " + empId)
    );
    employeeRepository.deleteById(empId);
  }

  private Set<Department> resolveDepartments(List<Integer> departmentIds) {
    Set<Department> departments = new HashSet<>();
    for (Integer deptId : departmentIds) {
      Department department = departmentRepository.findById(deptId)
          .orElseThrow(() ->
              new ResourceNotFoundException("Department does not exist with given id: " + deptId));
      departments.add(department);
    }
    return departments;
  }
}
