package net.javaguides.ems.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "department")
public class Department {

  @Id
  @Column(name = "deptid")
  private Integer deptId;

  @Column(name = "deptname")
  private String deptName;

  // inverse side — Employee owns the join table
  @ManyToMany(mappedBy = "departments")
  private Set<Employee> employees = new HashSet<>();
}
