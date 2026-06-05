package net.javaguides.ems.repository;

import net.javaguides.ems.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

//JpaRepository<type of entity, type of primary key>
//EmployeeRepository inherits the contract from JpaRepository. -- interface extends interface
/*SimpleJpaRepository  is already annotated with @annotation so do not need annotate repository
also annotated with @transactional so do not need add @transactional
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
