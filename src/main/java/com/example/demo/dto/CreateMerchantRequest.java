package com.example.demo.dto;


import lombok.Data;

@Data
public class CreateMerchantRequest {
    private String merchantName;
    private String contactNumber;
    private String businessName;
    private String brandName; // The name of the brand the merchant belongs to
}
