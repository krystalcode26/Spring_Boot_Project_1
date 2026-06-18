package net.javaguides.ems.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.javaguides.ems.dto.StaffDto;
import net.javaguides.ems.service.StaffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/staffs")
public class StaffController {

  private final StaffService staffService;

  @PostMapping
  public ResponseEntity<StaffDto> createStaff(@Valid @RequestBody StaffDto staffDto){
    StaffDto savedStaff = staffService.createStaff(staffDto);
    return new ResponseEntity<>(savedStaff, HttpStatus.CREATED);
  }

  @GetMapping("{id}")
  public ResponseEntity<StaffDto> getStaffById(@PathVariable("id") Long staffId){
    StaffDto staffDto = staffService.getStaffById(staffId);
    return ResponseEntity.ok(staffDto);
  }

  @GetMapping
  public ResponseEntity<List<StaffDto>> getAllStaff() {
    List<StaffDto> staffs = staffService.getAllStaff();
    return ResponseEntity.ok(staffs);
  }

  @PutMapping("{id}")
  public ResponseEntity<StaffDto> updateStaff(@PathVariable("id") Long id,
                                              @Valid @RequestBody StaffDto updatedStaff){
    StaffDto staffDto = staffService.updateStaff(id, updatedStaff);
    return ResponseEntity.ok(staffDto);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<String> deleteStaff(@PathVariable("id")Long id){
    staffService.deleteStaff(id);
    return ResponseEntity.ok("Staff deleted successfully");
  }
}
