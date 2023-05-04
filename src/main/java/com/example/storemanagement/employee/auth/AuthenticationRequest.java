package com.example.storemanagement.employee.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class AuthenticationRequest {
    private String email;
     String password;
}
