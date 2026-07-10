package net.javaguides.ems.repository;

import net.javaguides.ems.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

  @Override
  @EntityGraph(attributePaths = "departments")
  Optional<Employee> findById(Long empId);

  @EntityGraph(attributePaths = "departments")
  Optional<Employee> findByEmail(String email);

  @Override
  @EntityGraph(attributePaths = "departments")
  List<Employee> findAll();

  @Override
  @EntityGraph(attributePaths = "departments")
  Page<Employee> findAll(Pageable pageable);

  @EntityGraph(attributePaths = "departments")
  Page<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
      String firstName,
      String lastName,
      String email,
      String department,
      Pageable pageable);
}
