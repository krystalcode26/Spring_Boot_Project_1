package net.javaguides.ems.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee")
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "empid")
  private Long empId;

  @Column(name = "empname", nullable = false)
  private String empName;

  @Column(name = "firstname")
  private String firstName;

  @Column(name = "lastname")
  private String lastName;

  @Column(name = "age")
  private Integer age;

  @Column(name = "salary")
  private BigDecimal salary;

  @Column(name = "email")
  private String email;

  @Column(name = "password")
  private String password;

  @Column(name = "role")
  private String role;

  @ManyToMany
  @JoinTable(
      name = "employee_department",
      joinColumns = @JoinColumn(name = "empid"),
      inverseJoinColumns = @JoinColumn(name = "deptid")
  )
  private Set<Department> departments = new HashSet<>();
}
