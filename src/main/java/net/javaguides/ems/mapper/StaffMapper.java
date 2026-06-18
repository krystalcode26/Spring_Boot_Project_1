package net.javaguides.ems.mapper;

import net.javaguides.ems.dto.StaffDto;
import net.javaguides.ems.entity.Staff;

public class StaffMapper {

  public static StaffDto mapToStaffDto(Staff staff) {
    return new StaffDto(
        staff.getId(),
        staff.getFirstName(),
        staff.getLastName(),
        staff.getEmail()
    );
  }

  public static Staff mapToStaff(StaffDto staffDto) {
    return new Staff(
        staffDto.getId(),
        staffDto.getFirstName(),
        staffDto.getLastName(),
        staffDto.getEmail()
    );
  }
}
