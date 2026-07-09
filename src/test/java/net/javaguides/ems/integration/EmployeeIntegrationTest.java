package net.javaguides.ems.integration;

import net.javaguides.ems.dto.DepartmentDto;
import net.javaguides.ems.dto.EmployeeDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeIntegrationTest {

  @Autowired
  private RestTestClient restClient;

  private static Integer departmentId;
  private static Long employeeId;

  @Test
  @Order(1)
  void createDepartment_returns201() {
    DepartmentDto department = new DepartmentDto(1, "Engineering");

    restClient.post()
        .uri("/api/departments")
        .contentType(MediaType.APPLICATION_JSON)
        .body(department)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(DepartmentDto.class)
        .value(dto -> {
          assertThat(dto).isNotNull();
          assertThat(dto.getDeptName()).isEqualTo("Engineering");
          departmentId = dto.getDeptId();
        });
  }

  @Test
  @Order(2)
  void createEmployee_returns201() {
    EmployeeDto employee = new EmployeeDto();
    employee.setFirstName("Integration");
    employee.setLastName("User");
    employee.setEmail("integration.user@example.com");
    employee.setDepartmentIds(List.of(departmentId));
    employee.setAge(30);
    employee.setSalary(new BigDecimal("75000"));

    restClient.post()
        .uri("/api/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .body(employee)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(EmployeeDto.class)
        .value(dto -> {
          assertThat(dto).isNotNull();
          assertThat(dto.getFirstName()).isEqualTo("Integration");
          assertThat(dto.getLastName()).isEqualTo("User");
          assertThat(dto.getEmail()).isEqualTo("integration.user@example.com");
          employeeId = dto.getEmpId();
        });
  }

  @Test
  @Order(3)
  void getEmployeeById_returns200() {
    restClient.get()
        .uri("/api/employees/" + employeeId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(EmployeeDto.class)
        .value(dto -> {
          assertThat(dto).isNotNull();
          assertThat(dto.getEmpId()).isEqualTo(employeeId);
          assertThat(dto.getFirstName()).isEqualTo("Integration");
          assertThat(dto.getLastName()).isEqualTo("User");
          assertThat(dto.getEmail()).isEqualTo("integration.user@example.com");
        });
  }

  @Test
  @Order(4)
  void updateEmployee_returns200() {
    EmployeeDto updated = new EmployeeDto();
    updated.setFirstName("Updated");
    updated.setLastName("User");
    updated.setEmail("updated.user@example.com");
    updated.setDepartmentIds(List.of(departmentId));
    updated.setAge(31);
    updated.setSalary(new BigDecimal("80000"));

    restClient.put()
        .uri("/api/employees/" + employeeId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(updated)
        .exchange()
        .expectStatus().isOk()
        .expectBody(EmployeeDto.class)
        .value(dto -> {
          assertThat(dto).isNotNull();
          assertThat(dto.getFirstName()).isEqualTo("Updated");
          assertThat(dto.getLastName()).isEqualTo("User");
        });
  }

  @Test
  @Order(5)
  void deleteEmployee_returns200() {
    restClient.delete()
        .uri("/api/employees/" + employeeId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .isEqualTo("Employee deleted successfully");
  }

  @Test
  @Order(6)
  void getEmployeeById_returns404_afterDelete() {
    restClient.get()
        .uri("/api/employees/" + employeeId)
        .exchange()
        .expectStatus().isNotFound();
  }
}
