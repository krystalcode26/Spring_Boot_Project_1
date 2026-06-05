package net.javaguides.ems.controller;

import lombok.AllArgsConstructor;
import net.javaguides.ems.dto.EmployeeDto;
import net.javaguides.ems.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//Controller layer is depend on service layer so need to change service layer first then controller layer

@AllArgsConstructor
//@ - handle HTTP request
@RestController
//@ - base URL for all rest apis that we are going to build with this controller
@RequestMapping("/api/employees")
public class EmployeeController {

  //inject dependency
  private EmployeeService employeeService;

  //Build Add Employee REST API - create a method, make method as a rest API by annotation
  //@RequestBody - extract Json from HTTP request and convert Json to EmployeeDto java object
  //              postman - json data needs to be the same as EmployeeDto field's name
  @PostMapping
  public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto){
    EmployeeDto savedEmployee = employeeService.createEmployee(employeeDto);
    return new ResponseEntity<>(savedEmployee,  HttpStatus.CREATED);
  }

  //Build Get Employee REST API, {id} - URI template variable
  @GetMapping("{id}")
  public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("id") Long employeeId){
    EmployeeDto employeeDto = employeeService.getEmployeeById((employeeId));
    return ResponseEntity.ok(employeeDto);
  }

  //Build Get All Employees REST API
  @GetMapping
  public ResponseEntity<List<EmployeeDto>> getAllEmployees(){
    List<EmployeeDto> employees = employeeService.getAllEmployees();
    return ResponseEntity.ok(employees);
  }

  // Build update Employee REST API, @RequestBody - extract json from request body
  @PutMapping("{id}")
  public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable("id") Long employeeId,
                                                    @RequestBody EmployeeDto updatedEmployee){
    EmployeeDto employeeDto = employeeService.updateEmployee(employeeId, updatedEmployee);
    return ResponseEntity.ok(employeeDto);
  }

  //BuIld delete employee REST API
  @DeleteMapping("{id}")
  public ResponseEntity<String> deleteEmployee(@PathVariable("id") Long employeeId){
    employeeService.deleteEmployee(employeeId);
    return ResponseEntity.ok("Employee deledte successfully");
  }
}
