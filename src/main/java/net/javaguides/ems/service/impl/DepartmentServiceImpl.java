package net.javaguides.ems.service.impl;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.dto.DepartmentDto;
import net.javaguides.ems.entity.Department;
import net.javaguides.ems.exception.ResourceNotFoundException;
import net.javaguides.ems.mapper.DepartmentMapper;
import net.javaguides.ems.repository.DepartmentRepository;
import net.javaguides.ems.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

  private final DepartmentRepository departmentRepository;

  @Override
  public DepartmentDto createDepartment(DepartmentDto departmentDto) {
    Department department = DepartmentMapper.mapToDepartment(departmentDto);
    Department savedDepartment = departmentRepository.save(department);
    return DepartmentMapper.mapToDepartmentDto(savedDepartment);
  }

  @Override
  public DepartmentDto getDepartmentById(Integer deptId) {
    Department department = departmentRepository.findById(deptId)
        .orElseThrow(() ->
            new ResourceNotFoundException("Department does not exist with given id: " + deptId));
    return DepartmentMapper.mapToDepartmentDto(department);
  }

  @Override
  public List<DepartmentDto> getAllDepartments() {
    return departmentRepository.findAll().stream()
        .map(DepartmentMapper::mapToDepartmentDto)
        .collect(Collectors.toList());
  }
}
