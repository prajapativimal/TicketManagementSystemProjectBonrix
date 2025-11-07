package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor   // generates constructor with all fields
@NoArgsConstructor    // generates default constructor
public class AuthResponse {
    private String token;
    private String role;
}
