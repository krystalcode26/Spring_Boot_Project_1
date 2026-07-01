package net.javaguides.ems.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {

  private Long empId;

  @NotBlank(message = "Employee name is required")
  private String empName;

  @NotNull(message = "Department id is required")
  private Long deptId;

  @Min(value = 18, message = "Age must be at least 18")
  private Integer age;

  @Positive(message = "Salary must be greater than 0")
  private BigDecimal salary;
}
