package net.javaguides.ems.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.javaguides.ems.entity.Department;
import net.javaguides.ems.entity.Employee;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class DepartmentRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public Department save(Department department) {
    if (department.getDeptId() == null) {
      Integer nextId = entityManager
          .createQuery("SELECT COALESCE(MAX(d.deptId), 0) + 1 FROM Department d", Integer.class)
          .getSingleResult();
      department.setDeptId(nextId);
      entityManager.persist(department);
      entityManager.flush();
      return department;
    }
    return entityManager.merge(department);
  }

  @Transactional(readOnly = true)
  public Optional<Department> findById(Integer deptId) {
    return Optional.ofNullable(entityManager.find(Department.class, deptId));
  }

  @Transactional(readOnly = true)
  public List<Department> findAll() {
    return entityManager
        .createQuery("SELECT d FROM Department d ORDER BY d.deptId", Department.class)
        .getResultList();
  }

  @Transactional
  public void deleteById(Integer deptId) {
    Department department = entityManager.find(Department.class, deptId);
    if (department == null) {
      return;
    }

    List<Employee> linkedEmployees = entityManager.createQuery(
            "SELECT e FROM Employee e JOIN e.departments d WHERE d.deptId = :deptId",
            Employee.class)
        .setParameter("deptId", deptId)
        .getResultList();

    for (Employee employee : linkedEmployees) {
      employee.getDepartments().remove(department);
    }

    entityManager.remove(department);
    entityManager.flush();
  }
}
