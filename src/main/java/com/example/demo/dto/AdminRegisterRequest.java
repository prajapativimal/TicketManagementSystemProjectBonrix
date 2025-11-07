package com.example.demo.dto;

import lombok.Data;

@Data
public class AdminRegisterRequest {
    private String name;
    private String email;
    private String password;

}
