package com.example.storemanagement.employee;


import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.logging.Log;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;


    private Log log = LogFactory.getLog(EmployeeController.class);



    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    public Map<String, Object> getEmployeeFields(Employee employee) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", employee.getId());
        fields.put("firstName", employee.getFirstName());
        fields.put("lastName", employee.getLastName());
        fields.put("email", employee.getEmail());
        fields.put("dob", employee.getDob());
        fields.put("address", employee.getAddress());
        fields.put("roles", employee.getAuthorities());
        return fields;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findEmployeeById(@PathVariable("id") Long id) {
        Employee employee = employeeService.findEmployeeById(id);
        Map<String, Object> Employee = getEmployeeFields(employee);
        return new ResponseEntity<>(Employee, HttpStatus.OK);
    }

//    @GetMapping("/email/{email}")
//    @RolesAllowed("ADMIN")
//    public ResponseEntity<Map<String, Object>>  findEmployeeByEmail(@PathVariable("email") String email) {
//        Employee employee = employeeService.findEmployeeByEmail(email);
//        Map<String, Object> Employee = getEmployeeFields(employee);
//        return new ResponseEntity<>(Employee, HttpStatus.OK);
//    }

    @PutMapping("/update")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Map<String, Object>>  updateEmployee(@RequestBody Employee employee) {
        Employee updatedEmployee = employeeService.updateEmployee(employee);
        Map<String, Object> Employee = getEmployeeFields(updatedEmployee);
        return new ResponseEntity<>(Employee, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    //@RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<?> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }



}
