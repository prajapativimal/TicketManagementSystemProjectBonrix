package com.example.demo.dto;

import lombok.Data;

@Data
public class UpdateBrandRequest {
    private String brandName;
    private String email;
    private String password; // Optional: only provide if changing it
    private String brandCategory;
    private String registeredAddress;
    private String pincode;
}