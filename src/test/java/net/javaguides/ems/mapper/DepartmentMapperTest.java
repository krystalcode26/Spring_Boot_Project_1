package net.javaguides.ems.mapper;

import net.javaguides.ems.dto.DepartmentDto;
import net.javaguides.ems.entity.Department;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DepartmentMapperTest {

  @Test
  void mapToDepartmentDto_mapsAllFields() {
    Department department = new Department(1, "Engineering", null);

    DepartmentDto dto = DepartmentMapper.mapToDepartmentDto(department);

    assertThat(dto.getDeptId()).isEqualTo(1);
    assertThat(dto.getDeptName()).isEqualTo("Engineering");
  }

  @Test
  void mapToDepartment_mapsAllFields() {
    DepartmentDto dto = new DepartmentDto(2, "HR");

    Department department = DepartmentMapper.mapToDepartment(dto);

    assertThat(department.getDeptId()).isEqualTo(2);
    assertThat(department.getDeptName()).isEqualTo("HR");
  }
}
