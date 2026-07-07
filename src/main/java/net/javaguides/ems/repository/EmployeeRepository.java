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
    List<Employee> results = entityManager.createQuery(
            "SELECT e FROM Employee e LEFT JOIN FETCH e.departments WHERE e.empId = :empId",
            Employee.class)
        .setParameter("empId", empId)
        .getResultList();
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Transactional(readOnly = true)
  public Optional<Employee> findByEmail(String email) {
    List<Employee> results = entityManager.createQuery(
            "SELECT e FROM Employee e LEFT JOIN FETCH e.departments WHERE e.email = :email",
            Employee.class)
        .setParameter("email", email)
        .getResultList();
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Transactional(readOnly = true)
  public List<Employee> findAll() {
    return entityManager.createQuery(
            "SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.departments ORDER BY e.empId",
            Employee.class)
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
