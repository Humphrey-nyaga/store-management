package com.example.storemanagement.employee.auth;


import com.example.storemanagement.employee.Employee;
import com.example.storemanagement.employee.EmployeeRepository;
import com.example.storemanagement.employee.EmployeeService;
import com.example.storemanagement.employee.Role;
import com.example.storemanagement.employee.exceptions.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final EmployeeRepository employeeRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
   private final  EmployeeService employeeService;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var employee = Employee.builder()
                .firstname(registerRequest.getFirstname())
                .lastname(registerRequest.getLastname())
                .address(registerRequest.getAddress())
                .email(registerRequest.getEmail())
                .dob(registerRequest.getDob())
                .phoneNumber(registerRequest.getPhoneNumber())
                .nationalID(registerRequest.getNationalID())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();
        employeeRepository.save(employee);
        var jwtToken = jwtService.generateToken(employee);
        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .employeeDTO(employeeService.findEmployeeByEmail(employee.getEmail()))
                .build();
    }
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest){
       authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                       authenticationRequest.getEmail(),
                       authenticationRequest.getPassword()
               )
       );
       var employee = employeeRepository.findByEmail(authenticationRequest.getEmail())
               .orElseThrow(()-> new EmployeeNotFoundException("Employee Does not Exist."));
        var jwtToken = jwtService.generateToken(employee);
        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .employeeDTO(employeeService.findEmployeeByEmail(employee.getEmail()))
                .build();
    }
}
