package net.javaguides.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
//create a constructor for this class
@AllArgsConstructor
@NoArgsConstructor

public class EmployeeDto{
  private Long id;
  private String firstName;
  private String lastName;
  private String email;

}
