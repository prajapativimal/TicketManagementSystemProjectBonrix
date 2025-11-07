package com.example.demo.dto;

import lombok.Data;

@Data
public class AdminLoginRequest {
    private String email;
    private String password;
}