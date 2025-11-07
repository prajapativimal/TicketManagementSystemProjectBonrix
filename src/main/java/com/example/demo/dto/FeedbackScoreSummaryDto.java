package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data 
@AllArgsConstructor
public class FeedbackScoreSummaryDto {
    private double averageScore;
    private long totalFeedbackCount;
}