package net.javaguides.ems.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.javaguides.ems.entity.Employee;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Hibernate via EntityManager — manual CRUD (no Spring Data JpaRepository)
@Repository
public class EmployeeRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public Employee save(Employee employee) {
    if (employee.getEmpId() == null) {
      entityManager.persist(employee);
      return employee;
    }
    return entityManager.merge(employee);
  }

  @Transactional(readOnly = true)
  public Optional<Employee> findById(Long empId) {
    return Optional.ofNullable(entityManager.find(Employee.class, empId));
  }

  @Transactional(readOnly = true)
  public List<Employee> findAll() {
    return entityManager
        .createQuery("SELECT e FROM Employee e ORDER BY e.empId", Employee.class)
        .getResultList();
  }

  @Transactional
  public void deleteById(Long empId) {
    Employee employee = entityManager.find(Employee.class, empId);
    if (employee != null) {
      entityManager.remove(employee);
    }
  }
}
