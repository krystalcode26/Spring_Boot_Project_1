package net.javaguides.ems.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.javaguides.ems.dto.DepartmentDto;
import net.javaguides.ems.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

  private final DepartmentService departmentService;

  @PostMapping
  public ResponseEntity<DepartmentDto> createDepartment(@Valid @RequestBody DepartmentDto departmentDto) {
    DepartmentDto savedDepartment = departmentService.createDepartment(departmentDto);
    return new ResponseEntity<>(savedDepartment, HttpStatus.CREATED);
  }

  @GetMapping("{id}")
  public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable("id") Integer deptId) {
    return ResponseEntity.ok(departmentService.getDepartmentById(deptId));
  }

  @GetMapping
  public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
    return ResponseEntity.ok(departmentService.getAllDepartments());
  }

  @PutMapping("{id}")
  public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable("id") Integer deptId,
                                                      @Valid @RequestBody DepartmentDto departmentDto) {
    return ResponseEntity.ok(departmentService.updateDepartment(deptId, departmentDto));
  }

  @DeleteMapping("{id}")
  public ResponseEntity<String> deleteDepartment(@PathVariable("id") Integer deptId) {
    departmentService.deleteDepartment(deptId);
    return ResponseEntity.ok("Department deleted successfully");
  }
}
