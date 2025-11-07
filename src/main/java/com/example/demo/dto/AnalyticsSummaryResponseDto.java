package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class AnalyticsSummaryResponseDto {
    private List<TicketVolumeByGroupDto> ticketVolumeByCategory;
    private List<TicketVolumeByGroupDto> ticketVolumeByStatus;
    private List<TicketVolumeByGroupDto> ticketVolumeByPriority;
    private SlaComplianceReportDto slaCompliance;
    private AverageResolutionTimeDto averageResolutionTime;
    private FeedbackScoreSummaryDto feedbackScoreSummary;
}