package net.javaguides.ems.service.impl;

import lombok.RequiredArgsConstructor;
import net.javaguides.ems.dto.StaffDto;
import net.javaguides.ems.entity.Staff;
import net.javaguides.ems.exception.ResourceNotFoundException;
import net.javaguides.ems.mapper.StaffMapper;
import net.javaguides.ems.repository.StaffRepository;
import net.javaguides.ems.service.StaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

  private static final Logger log = LoggerFactory.getLogger(StaffServiceImpl.class);

  private final StaffRepository staffRepository;

  @Override
  // @CacheEvict(clear cache on changes) is a Spring annotation that allows you to evict the cache after a method call
  // clears the staffs cache after a write operation
  @CacheEvict(value = "staffs", allEntries = true)
  public StaffDto createStaff(StaffDto staffDto) {
    log.info("Creating staff with email={}", staffDto.getEmail());
    Staff staff = StaffMapper.mapToStaff(staffDto);
    //id set to null so the repository treats it as an insert
    staff.setId(null);
    Staff savedStaff = staffRepository.save(staff);
    return StaffMapper.mapToStaffDto(savedStaff);
  }

  @Override
  // @Cacheable(store read result) is a Spring annotation that allows you to cache the results of a method call
  /*
  First call getStaffById(1)
  Cache miss → method runs → JDBC query runs
  Return value stored in cache staffs with key 1
  You see: "Fetching staff from database, id=1"

  Second call getStaffById(1) (within 10 minutes)
  You see: "Fetching staff from cache, id=1"
  Cache hit → method body does not run
  No log, no SQL — cached StaffDto returned

  Third call getStaffById(2) (not in cache)
  Cache miss → method runs → JDBC query runs
  Return value stored in cache staffs with key 2
  You see: "Fetching staff from database, id=2"
  */
  @Cacheable(value = "staffs", key = "#staffId")
  public StaffDto getStaffById(Long staffId) {
    log.info("Fetching staff from database, id={}", staffId);
    Staff staff = staffRepository.findById(staffId)
        .orElseThrow(() ->
            new ResourceNotFoundException("Staff does not exist with given id: " + staffId));
    return StaffMapper.mapToStaffDto(staff);
  }

  @Override
  @Cacheable(value = "staffs", key = "'all'")
  public List<StaffDto> getAllStaff() {
    log.info("Fetching all staff from database");
    List<Staff> staffs = staffRepository.findAll();

    return staffs.stream()
        .map(StaffMapper::mapToStaffDto)
        .collect(Collectors.toList());
  }

  @Override
  @CacheEvict(value = "staffs", allEntries = true)
  public StaffDto updateStaff(Long staffId, StaffDto updatedStaff) {
    log.info("Updating staff id={}", staffId);
    Staff staff = staffRepository.findById(staffId).orElseThrow(
        () -> new ResourceNotFoundException("Staff does not exist with given id: " + staffId)
    );

    staff.setFirstName(updatedStaff.getFirstName());
    staff.setLastName(updatedStaff.getLastName());
    staff.setEmail(updatedStaff.getEmail());

    Staff savedStaff = staffRepository.save(staff);
    return StaffMapper.mapToStaffDto(savedStaff);
  }

  @Override
  @CacheEvict(value = "staffs", allEntries = true)
  public void deleteStaff(Long id) {
    log.info("Deleting staff id={}", id);
    staffRepository.findById(id).orElseThrow(
        () -> new ResourceNotFoundException("Staff does not exist with given id: " + id)
    );
    staffRepository.deleteById(id);
  }
}
