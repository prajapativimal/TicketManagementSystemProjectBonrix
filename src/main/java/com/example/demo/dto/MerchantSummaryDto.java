// In a new file: dto/MerchantSummaryDto.java
package com.example.demo.dto;

public record MerchantSummaryDto(
    Long id,
    String merchantName,
    String contactNumber
) {}