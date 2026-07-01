package net.javaguides.ems.service;

import net.javaguides.ems.dto.StaffDto;

import java.util.List;

public interface StaffService {
  StaffDto createStaff(StaffDto staffDto);
  StaffDto getStaffById(Long id);
  List<StaffDto> getAllStaff();
  StaffDto updateStaff(Long id, StaffDto updatedStaff);
  void deleteStaff(Long id);

}
