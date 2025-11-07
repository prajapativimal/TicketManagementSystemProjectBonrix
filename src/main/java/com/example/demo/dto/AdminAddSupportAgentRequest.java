package com.example.demo.dto;

import lombok.Data;

@Data
public class AdminAddSupportAgentRequest 
{
	private String name;
    private String email;
    private String password;
    
 // ✅ ADD THIS FIELD
    private String brandName;

}
