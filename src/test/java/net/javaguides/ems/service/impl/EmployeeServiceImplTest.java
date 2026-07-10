package net.javaguides.ems.service.impl;

import net.javaguides.ems.dto.EmployeeDto;
import net.javaguides.ems.dto.PagedResponse;
import net.javaguides.ems.entity.Department;
import net.javaguides.ems.entity.Employee;
import net.javaguides.ems.exception.ResourceNotFoundException;
import net.javaguides.ems.kafka.EmployeeEventProducer;
import net.javaguides.ems.kafka.EmployeeEventType;
import net.javaguides.ems.repository.DepartmentRepository;
import net.javaguides.ems.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private DepartmentRepository departmentRepository;

  @Mock
  private ObjectProvider<EmployeeEventProducer> employeeEventProducerProvider;

  @Mock
  private EmployeeEventProducer employeeEventProducer;

  @InjectMocks
  private EmployeeServiceImpl employeeService;

  @Test
  void createEmployee_resolvesDepartmentsAndSaves() {
    EmployeeDto request = new EmployeeDto(
        null, "Alice", "Smith", "alice@example.com", "Engineering", List.of(1), 30, new BigDecimal("75000"));
    Department department = new Department(1, "Engineering", null);
    Employee saved = new Employee();
    saved.setEmpId(1L);
    saved.setFirstName("Alice");
    saved.setLastName("Smith");
    saved.setEmpName("Alice Smith");
    saved.setEmail("alice@example.com");
    saved.setDepartment("Engineering");
    saved.setAge(30);
    saved.setSalary(new BigDecimal("75000"));
    saved.setDepartments(Set.of(department));

    when(departmentRepository.findById(1)).thenReturn(Optional.of(department));
    when(employeeRepository.save(any(Employee.class))).thenReturn(saved);
    when(employeeEventProducerProvider.getIfAvailable()).thenReturn(employeeEventProducer);
    when(employeeEventProducer.publish(eq(EmployeeEventType.CREATED), any(Employee.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    EmployeeDto response = employeeService.createEmployee(request);

    assertThat(response.getEmpId()).isEqualTo(1L);
    assertThat(response.getFirstName()).isEqualTo("Alice");
    assertThat(response.getEmail()).isEqualTo("alice@example.com");
    assertThat(response.getDepartmentIds()).containsExactly(1);
    verify(departmentRepository).findById(1);
    verify(employeeEventProducer).publish(eq(EmployeeEventType.CREATED), any(Employee.class));
  }

  @Test
  void getEmployeeById_returnsDtoWhenFound() {
    Employee employee = new Employee();
    employee.setEmpId(1L);
    employee.setFirstName("Alice");
    employee.setLastName("Smith");
    employee.setEmpName("Alice Smith");
    employee.setEmail("alice@example.com");
    employee.setDepartments(Set.of());

    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

    EmployeeDto dto = employeeService.getEmployeeById(1L);

    assertThat(dto.getEmpId()).isEqualTo(1L);
    assertThat(dto.getFirstName()).isEqualTo("Alice");
    assertThat(dto.getLastName()).isEqualTo("Smith");
    verify(employeeRepository).findById(1L);
  }

  @Test
  void getEmployeeById_throwsWhenNotFound() {
    when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("99");
  }

  @Test
  void getAllEmployees_returnsMappedList() {
    Employee employee = new Employee();
    employee.setEmpId(1L);
    employee.setFirstName("Alice");
    employee.setLastName("Smith");
    employee.setEmpName("Alice Smith");
    employee.setDepartments(Set.of());
    when(employeeRepository.findAll()).thenReturn(List.of(employee));

    List<EmployeeDto> employees = employeeService.getAllEmployees();

    assertThat(employees).hasSize(1);
    assertThat(employees.get(0).getFirstName()).isEqualTo("Alice");
  }

  @Test
  void deleteEmployee_deletesWhenFound() {
    Employee employee = new Employee();
    employee.setEmpId(1L);
    employee.setEmpName("Alice Smith");
    employee.setDepartments(Set.of());
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
    when(employeeEventProducerProvider.getIfAvailable()).thenReturn(employeeEventProducer);
    when(employeeEventProducer.publish(eq(EmployeeEventType.DELETED), any(Employee.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    employeeService.deleteEmployee(1L);

    verify(employeeRepository).deleteById(1L);
    verify(employeeEventProducer).publish(eq(EmployeeEventType.DELETED), any(Employee.class));
  }

  @Test
  void updateEmployee_updatesDepartmentsAndFields() {
    Employee existing = new Employee();
    existing.setEmpId(1L);
    existing.setFirstName("Alice");
    existing.setLastName("Smith");
    existing.setEmpName("Alice Smith");
    existing.setEmail("alice@example.com");
    existing.setDepartment("Engineering");
    existing.setAge(30);
    existing.setSalary(new BigDecimal("75000"));
    existing.setDepartments(Set.of());

    Department engineering = new Department(2, "Engineering", null);
    EmployeeDto update = new EmployeeDto(
        null, "Alice", "Updated", "alice.updated@example.com", "HR", List.of(2), 31, new BigDecimal("80000"));
    Employee saved = new Employee();
    saved.setEmpId(1L);
    saved.setFirstName("Alice");
    saved.setLastName("Updated");
    saved.setEmpName("Alice Updated");
    saved.setEmail("alice.updated@example.com");
    saved.setDepartment("HR");
    saved.setAge(31);
    saved.setSalary(new BigDecimal("80000"));
    saved.setDepartments(Set.of(engineering));

    when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(departmentRepository.findById(2)).thenReturn(Optional.of(engineering));
    when(employeeRepository.save(any(Employee.class))).thenReturn(saved);
    when(employeeEventProducerProvider.getIfAvailable()).thenReturn(employeeEventProducer);
    when(employeeEventProducer.publish(eq(EmployeeEventType.UPDATED), any(Employee.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    EmployeeDto response = employeeService.updateEmployee(1L, update);

    assertThat(response.getLastName()).isEqualTo("Updated");
    assertThat(response.getEmail()).isEqualTo("alice.updated@example.com");
    assertThat(response.getDepartment()).isEqualTo("HR");
    assertThat(response.getDepartmentIds()).containsExactly(2);
    verify(employeeEventProducer).publish(eq(EmployeeEventType.UPDATED), any(Employee.class));
  }

  @Test
  void updateEmployee_throwsWhenEmployeeNotFound() {
    when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
    EmployeeDto update = new EmployeeDto(
        null, "X", "Y", "x@example.com", "Ops", List.of(1), 20, new BigDecimal("1"));

    assertThatThrownBy(() -> employeeService.updateEmployee(99L, update))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void createEmployee_throwsWhenDepartmentMissing() {
    when(departmentRepository.findById(99)).thenReturn(Optional.empty());
    EmployeeDto request = new EmployeeDto(
        null, "Alice", "Smith", "alice@example.com", "Engineering", List.of(99), 30, new BigDecimal("75000"));

    assertThatThrownBy(() -> employeeService.createEmployee(request))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("99");
  }

  @Test
  void getEmployeesPaged_returnsMatchingPage() {
    Employee employee = new Employee();
    employee.setEmpId(1L);
    employee.setFirstName("Alice");
    employee.setLastName("Smith");
    employee.setEmail("alice@example.com");
    employee.setDepartment("Engineering");
    employee.setDepartments(Set.of());

    Pageable pageable = PageRequest.of(0, 5);
    when(employeeRepository
        .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
            "alice", "alice", "alice", "alice", pageable))
        .thenReturn(new PageImpl<>(List.of(employee), pageable, 1));

    PagedResponse<EmployeeDto> response = employeeService.getEmployeesPaged("alice", pageable);

    assertThat(response.getContent()).hasSize(1);
    assertThat(response.getContent().get(0).getFirstName()).isEqualTo("Alice");
    assertThat(response.getTotalElements()).isEqualTo(1);
    assertThat(response.getPage()).isEqualTo(0);
  }

  @Test
  void getEmployeesPaged_blankQuery_usesFindAll() {
    Pageable pageable = PageRequest.of(0, 5);
    when(employeeRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

    PagedResponse<EmployeeDto> response = employeeService.getEmployeesPaged("  ", pageable);

    assertThat(response.getContent()).isEmpty();
    assertThat(response.getTotalElements()).isZero();
    verify(employeeRepository).findAll(pageable);
  }

  @Test
  void deleteEmployee_throwsWhenNotFound() {
    when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> employeeService.deleteEmployee(99L))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
