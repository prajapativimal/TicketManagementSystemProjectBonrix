package com.example.demo.dto;

import lombok.Data;

@Data
public class SupportAgentLoginRequest {
    private String email;
    private String password;
}