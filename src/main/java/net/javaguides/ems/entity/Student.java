package net.javaguides.ems.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

//is a Lombok annotation that generates a constructor with no arguments. 
// It is commonly used with JPA entities because Hibernate requires a default constructor to instantiate objects through reflection.
@NoArgsConstructor

//Lombok annotation that automatically generates a constructor containing all fields of the class.

@AllArgsConstructor
@Entity
@Table(name = "students")
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "email", nullable = false, unique = true)
  private String email;
}
