package com.example.storemanagement.employee;

import com.example.storemanagement.employee.Mail.EmployeeEmailService;
import com.example.storemanagement.employee.auth.EmployeeLoginRequest;
import org.apache.commons.logging.LogFactory;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeEmailService employeeEmailService;

    //private final TokenAuthenticationService tokenAuthenticationService;

    private Log log = LogFactory.getLog(EmployeeController.class);

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable();
        return http.build();
    }

    public EmployeeController(EmployeeService employeeService, PasswordEncoder passwordEncoder, EmployeeEmailService employeeEmailService ) {
        this.employeeService = employeeService;
        this.passwordEncoder = passwordEncoder;
        this.employeeEmailService = employeeEmailService;
    }

    @PostMapping("/")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        String encodedPassword = passwordEncoder.encode(employee.getPassword());
        employee.setPassword(encodedPassword);
        Employee newEmployee = employeeService.createEmployee(employee);

        log.info("============USER CREATED=============");
        //TODO - Reconfigure this email registration to work
        //String activationLink = "http://localhost.com:8080/activate?employeeId=" + newEmployee.getId();
        //employeeEmailService.sendRegistrationEmail(newEmployee, activationLink);
       // log.info("============USER ACTIVATION EMAIL SENT=============");
        //ToDO - response should not include hashed password
        return ResponseEntity.status(HttpStatus.CREATED).body(newEmployee);
    }

    public Map<String, Object> getEmployeeFields(Employee employee) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", employee.getId());
        fields.put("firstName", employee.getFirstName());
        fields.put("lastName", employee.getLastName());
        fields.put("email", employee.getEmail());
        fields.put("dob", employee.getDob());
        fields.put("address", employee.getAddress());
        fields.put("roles", employee.getRoles());
        return fields;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findEmployeeById(@PathVariable("id") Long id) {
        Employee employee = employeeService.findEmployeeById(id);
        Map<String, Object> Employee = getEmployeeFields(employee);
        return new ResponseEntity<>(Employee, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    //@RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<Map<String, Object>>  findEmployeeByEmail(@PathVariable("email") String email) {
        Employee employee = employeeService.findEmployeeByEmail(email);
        Map<String, Object> Employee = getEmployeeFields(employee);
        return new ResponseEntity<>(Employee, HttpStatus.OK);
    }

    @PutMapping("/update")
    //@RolesAllowed("ROLE_ADMIN")
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

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>>login(@RequestBody EmployeeLoginRequest loginRequest, HttpServletResponse response) {
        Employee employee = employeeService.findEmployeeByEmail(loginRequest.getEmail());
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String enteredPassword = loginRequest.getPassword();
        String storedPassword = employee.getPassword();

        if (passwordEncoder.matches(enteredPassword, storedPassword)) {
            Map<String, Object> newEmployee = getEmployeeFields(employee);
        //TODO We need to have  JWT returned together with the response
            return ResponseEntity.ok(newEmployee);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



    //ToDO - response should not include hashed password \n
        // TODO - Return a JWT token after successful login


}
