package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor
public class AverageResolutionTimeDto {
    private double averageHours;
    private String formattedDuration;
}