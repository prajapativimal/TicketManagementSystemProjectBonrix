package com.example.demo.dto;


import lombok.Data;

@Data
public class AdminCreateBrandRequest
{
	private String brandName;
    private String email;
    private String password;
    private String brandCategory;
    private String registeredAddress;
    private String pincode;
    
 // ✅ ADD THIS FIELD
    private String contactNumber;

}
