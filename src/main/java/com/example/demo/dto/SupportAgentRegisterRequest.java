package com.example.demo.dto;


import lombok.Data;

@Data
public class SupportAgentRegisterRequest {
    private String name;
    private String email;
    private String password;
}