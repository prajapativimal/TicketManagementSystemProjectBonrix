package com.example.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class AdminPerformanceReportDto
{
	private Long agentId;
    private String agentName;
    private long resolvedTicketsCount;
    private double averageResolutionHours;
    // ✅ ADD THIS FIELD
    private List<AgentTicketDetailDto> resolvedTickets;

}
