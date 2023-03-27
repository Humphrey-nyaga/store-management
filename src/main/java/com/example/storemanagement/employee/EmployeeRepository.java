package com.example.storemanagement.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findById(Long id);
    Optional<Employee> findByEmail(String email);

    void deleteEmployeeById(Long id);
    void deleteEmployeeByEmail(String email);



}
