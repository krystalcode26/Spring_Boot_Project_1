package net.javaguides.ems.controller;

import lombok.AllArgsConstructor;
import net.javaguides.ems.dto.StudentDto;
import net.javaguides.ems.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/*Annotation: predefined logic that compiler knows what to do */
@AllArgsConstructor
@RestController
@RequestMapping("/api/students")
public class StudentController {
  // Constructor Injection
  //  public StudentController(StudentService studentService) {
  //    this.studentService = studentService;
  //  }

  //setter injection - another setter method
  //public void setStudentService(StudentService studentService){
  //    this.studentService = studentService;
  // }

  //field injection
  //studentService here is a Spring Bean object that we defined and configured at service annotation on student service implementation
  //class template which inherits the student service interface. -> studentService object No need to instantiate.(IOC)
  private final StudentService studentService;

  //Build Add Student REST API
  @PostMapping
  public ResponseEntity<StudentDto> createStudent(@RequestBody StudentDto studentDto) {
    // casting the request to the service layer (studentService) to call APIs(studentDto) to process use requests.
    StudentDto savedStudent = studentService.createStudent(studentDto);
    return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
  }

  //Build Get student REST API
  @GetMapping("{id}")
  public ResponseEntity<StudentDto> getStudentById(@PathVariable("id") Long studentId) {
    StudentDto studentDto = studentService.getStudentById(studentId);
    return ResponseEntity.ok(studentDto);
  }

  //Build Get All Students REST API
  @GetMapping
  public ResponseEntity<List<StudentDto>> getAllStudents() {
    //Integer i1 = Integer.valueOf(1);
    List<StudentDto> students = studentService.getAllStudents();
    return ResponseEntity.ok(students);
  }

  //Build Update students REST API
  @PutMapping("{id}")
  public ResponseEntity<StudentDto> updateStudent(@PathVariable("id") Long studentId,
                                                  @RequestBody StudentDto updatedStudent) {
    StudentDto studentDto = studentService.updateStudent(studentId, updatedStudent);
    return ResponseEntity.ok(studentDto);
  }

  //Data payload - the actual data transmitted between a client and server in an HTTP request or response.
  //@RequestHeader
  //@RequestParam

  //Build Delete students REST API
  @DeleteMapping("{id}")
  public ResponseEntity<String> deleteStudent(@PathVariable("id") Long studentId) {
    studentService.deleteStudent(studentId);
    return ResponseEntity.ok("Student deleted successfully");
  }


}
