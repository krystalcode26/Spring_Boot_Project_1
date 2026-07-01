package net.javaguides.ems.mapper;

import net.javaguides.ems.dto.DepartmentDto;
import net.javaguides.ems.entity.Department;

public class DepartmentMapper {

  public static DepartmentDto mapToDepartmentDto(Department department) {
    return new DepartmentDto(department.getDeptId(), department.getDeptName());
  }

  public static Department mapToDepartment(DepartmentDto departmentDto) {
    return new Department(
        departmentDto.getDeptId(),
        departmentDto.getDeptName(),
        null
    );
  }
}
