package com.example.storemanagement.employee.auth;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String address;
    private String dob;
    private String email;
    private String password;
    private String phoneNumber;
    private String nationalID;
}
