package com.example.storemanagement.employee;

import com.example.storemanagement.employee.Mail.EmployeeEmailService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeEmailService employeeEmailService;


    public EmployeeController(EmployeeService employeeService, PasswordEncoder passwordEncoder, EmployeeEmailService employeeEmailService) {
        this.employeeService = employeeService;
        this.passwordEncoder = passwordEncoder;
        this.employeeEmailService = employeeEmailService;
    }

    @PostMapping("/")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(employee.getPassword());
        employee.setPassword(encodedPassword);
        Employee newEmployee = employeeService.createEmployee(employee);

        String activationLink = "http://localhost.com:8080/activate?employeeId=" + newEmployee.getId();
        employeeEmailService.sendRegistrationEmail(newEmployee, activationLink);
        return ResponseEntity.status(HttpStatus.CREATED).body(newEmployee);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> findEmployeeById(@PathVariable("id") Long id) {
        Employee employee = employeeService.findEmployeeById(id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<Employee> findEmployeeByEmail(@PathVariable("email") String email) {
        Employee employee = employeeService.findEmployeeByEmail(email);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @PutMapping("/update")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<Employee> updateEmployee(@RequestBody Employee employee) {
        Employee updatedEmployee = employeeService.updateEmployee(employee);
        return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<?> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Employee> login(@RequestBody EmployeeLoginRequest loginRequest) {
        Employee employee = employeeService.findEmployeeByEmail(loginRequest.getEmail());
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String enteredPassword = loginRequest.getPassword();
        String storedPassword = employee.getPassword();

        if (passwordEncoder.matches(enteredPassword, storedPassword)) {
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


}
