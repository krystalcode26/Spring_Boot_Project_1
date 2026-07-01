package net.javaguides.ems.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.javaguides.ems.entity.Department;
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
      entityManager.persist(department);
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
}
