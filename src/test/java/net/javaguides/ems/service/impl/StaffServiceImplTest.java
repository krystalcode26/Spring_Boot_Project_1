package net.javaguides.ems.service.impl;

import net.javaguides.ems.dto.StaffDto;
import net.javaguides.ems.entity.Staff;
import net.javaguides.ems.exception.ResourceNotFoundException;
import net.javaguides.ems.repository.StaffRepository;
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
class StaffServiceImplTest {

  @Mock
  private StaffRepository staffRepository;

  @InjectMocks
  private StaffServiceImpl staffService;

  @Test
  void createStaff_savesAndReturnsDto() {
    StaffDto request = new StaffDto(null, "Jane", "Doe", "jane@example.com");
    Staff saved = new Staff(1L, "Jane", "Doe", "jane@example.com");
    when(staffRepository.save(any(Staff.class))).thenReturn(saved);

    StaffDto response = staffService.createStaff(request);

    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getEmail()).isEqualTo("jane@example.com");
  }

  @Test
  void getStaffById_returnsDtoWhenFound() {
    Staff staff = new Staff(1L, "Jane", "Doe", "jane@example.com");
    when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));

    StaffDto dto = staffService.getStaffById(1L);

    assertThat(dto.getFirstName()).isEqualTo("Jane");
  }

  @Test
  void getStaffById_throwsWhenNotFound() {
    when(staffRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> staffService.getStaffById(99L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void getAllStaff_returnsMappedList() {
    when(staffRepository.findAll()).thenReturn(List.of(
        new Staff(1L, "Jane", "Doe", "jane@example.com")));

    List<StaffDto> staff = staffService.getAllStaff();

    assertThat(staff).hasSize(1);
    assertThat(staff.get(0).getLastName()).isEqualTo("Doe");
  }

  @Test
  void deleteStaff_deletesWhenFound() {
    when(staffRepository.findById(1L)).thenReturn(Optional.of(
        new Staff(1L, "Jane", "Doe", "jane@example.com")));

    staffService.deleteStaff(1L);

    verify(staffRepository).deleteById(1L);
  }
}
