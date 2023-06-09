package com.example.storemanagement.employee;

import com.example.storemanagement.employee.exceptions.EmployeeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService implements UserDetailsService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository){

        this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(Employee employee){

        return employeeRepository.save(employee);
    }

    public Employee findEmployeeById(Long id){
        return employeeRepository.findById(id)
                .orElseThrow(()-> new EmployeeNotFoundException("Employee with id: " + " not found."));

    }

    public Employee findEmployeeByEmail(String email){
        return employeeRepository.findByEmail(email)
                .orElseThrow(()-> new EmployeeNotFoundException("Employee with Email: " + " not found."));

    }

    public Employee updateEmployee(Employee employee) {
        Optional<Employee> existingEmployee = employeeRepository.findById(employee.getId());

        if (existingEmployee.isPresent()) {
            return employeeRepository.save(employee);
        } else {
            // Create  a new employee if does not exist
            //throw new EmployeeNotFoundException("Employee with id " + employee.getId() + " not found");
            return createEmployee(employee);
        }
    }

     //TODO -  Consider deletion of employee by their email
    public void deleteEmployee(Long id) {
        Optional<Employee> existingEmployee = employeeRepository.findById(id);
        if (existingEmployee.isPresent()) {
             employeeRepository.deleteEmployeeById(id);
        } else {
            throw new EmployeeNotFoundException("Employee with id " + id + " not found");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User not found with email: " + email));
        return new User(employee.getEmail(), employee.getPassword(), new ArrayList<>());
    }

}
