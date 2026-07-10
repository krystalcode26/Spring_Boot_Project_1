package net.javaguides.ems.service.impl;

import net.javaguides.ems.dto.DepartmentDto;
import net.javaguides.ems.entity.Department;
import net.javaguides.ems.exception.ResourceNotFoundException;
import net.javaguides.ems.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

  @Mock
  private DepartmentRepository departmentRepository;

  @InjectMocks
  private DepartmentServiceImpl departmentService;

  @Test
  void getDepartmentById_returnsDtoWhenFound() {
    Department department = new Department(1, "Engineering", null);

    when(departmentRepository.findById(1)).thenReturn(Optional.of(department));

    DepartmentDto dto = departmentService.getDepartmentById(1);

    assertThat(dto.getDeptId()).isEqualTo(1);
    assertThat(dto.getDeptName()).isEqualTo("Engineering");
    verify(departmentRepository).findById(1);
  }

  @Test
  void getDepartmentById_throwsWhenNotFound() {
    when(departmentRepository.findById(99)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> departmentService.getDepartmentById(99))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("99");
  }

  @Test
  void createDepartment_savesAndReturnsDto() {
    DepartmentDto request = new DepartmentDto(null, "HR");
    Department saved = new Department(1, "HR", null);
    when(departmentRepository.save(any(Department.class))).thenReturn(saved);

    DepartmentDto response = departmentService.createDepartment(request);

    assertThat(response.getDeptId()).isEqualTo(1);
    assertThat(response.getDeptName()).isEqualTo("HR");
  }

  @Test
  void getAllDepartments_returnsMappedList() {
    when(departmentRepository.findAll()).thenReturn(List.of(new Department(1, "Engineering", null)));

    List<DepartmentDto> departments = departmentService.getAllDepartments();

    assertThat(departments).hasSize(1);
    assertThat(departments.get(0).getDeptName()).isEqualTo("Engineering");
  }

  @Test
  void updateDepartment_updatesNameWhenFound() {
    Department existing = new Department(1, "Engineering", null);
    DepartmentDto update = new DepartmentDto(null, "Platform");
    Department saved = new Department(1, "Platform", null);

    when(departmentRepository.findById(1)).thenReturn(Optional.of(existing));
    when(departmentRepository.save(existing)).thenReturn(saved);

    DepartmentDto response = departmentService.updateDepartment(1, update);

    assertThat(response.getDeptName()).isEqualTo("Platform");
    verify(departmentRepository).save(existing);
  }

  @Test
  void updateDepartment_throwsWhenNotFound() {
    when(departmentRepository.findById(99)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> departmentService.updateDepartment(99, new DepartmentDto(null, "X")))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void deleteDepartment_deletesWhenEmployeesLinked() {
    Department department = new Department(1, "Engineering", null);
    when(departmentRepository.findById(1)).thenReturn(Optional.of(department));

    departmentService.deleteDepartment(1);

    verify(departmentRepository).deleteById(1);
  }
}
