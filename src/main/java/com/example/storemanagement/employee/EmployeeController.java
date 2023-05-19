package com.example.storemanagement.employee;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.logging.Log;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;


    private Log log = LogFactory.getLog(EmployeeController.class);



    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> findEmployeeById(@PathVariable("id") Long id) {
        EmployeeDTO employeeDTO = employeeService.findEmployeeById(id);
        return new ResponseEntity<>(employeeDTO, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<EmployeeDTO>  findEmployeeByEmail(@PathVariable("email") String email) {
        EmployeeDTO employeeDTO = employeeService.findEmployeeByEmail(email);
        return new ResponseEntity<>(employeeDTO, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<EmployeeDTO>  updateEmployee(@RequestBody Employee employee) {
        EmployeeDTO employeeDTO = employeeService.updateEmployee(employee);
        return new ResponseEntity<>(employeeDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
