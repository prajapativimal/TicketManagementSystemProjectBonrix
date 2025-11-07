package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data 
@AllArgsConstructor
public class SlaComplianceReportDto {
    private long slaMetCount;
    private long slaMissedCount;
    private double complianceRate;
}