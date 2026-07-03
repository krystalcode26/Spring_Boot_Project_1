package net.javaguides.ems.mapper;

import net.javaguides.ems.dto.StaffDto;
import net.javaguides.ems.entity.Staff;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StaffMapperTest {

  @Test
  void mapToStaffDto_mapsAllFields() {
    Staff staff = new Staff(1L, "Jane", "Doe", "jane@example.com");

    StaffDto dto = StaffMapper.mapToStaffDto(staff);

    assertThat(dto.getId()).isEqualTo(1L);
    assertThat(dto.getFirstName()).isEqualTo("Jane");
    assertThat(dto.getLastName()).isEqualTo("Doe");
    assertThat(dto.getEmail()).isEqualTo("jane@example.com");
  }

  @Test
  void mapToStaff_mapsAllFields() {
    StaffDto dto = new StaffDto(2L, "Bob", "Lee", "bob@example.com");

    Staff staff = StaffMapper.mapToStaff(dto);

    assertThat(staff.getId()).isEqualTo(2L);
    assertThat(staff.getFirstName()).isEqualTo("Bob");
    assertThat(staff.getEmail()).isEqualTo("bob@example.com");
  }
}
