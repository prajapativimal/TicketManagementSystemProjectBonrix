package com.example.demo.dto;

import java.util.List;
import lombok.AllArgsConstructor; // ✅ Add this import
import lombok.Data;             // ✅ Add this import

@Data 
@AllArgsConstructor 
public class AdminAnalyticsSummaryDto 
{
	 private long totalTickets;
	    private long openTickets;
	    private long resolvedTicketsToday;
	    private List<TicketVolumeByGroupDto> ticketVolumeByCategory;
	  
	    private List<TicketVolumeByGroupDto> ticketVolumeByPriority;
	    
	    private SlaComplianceReportDto slaCompliance;
	    private List<AdminPerformanceReportDto> agentPerformance;
	    private FeedbackScoreSummaryDto feedbackScoreSummary;

	 
}
