package com.example.demo.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.Repository.FeedbackRepository;
import com.example.demo.dto.AdminAnalyticsSummaryDto;
import com.example.demo.dto.AdminPerformanceReportDto;
import com.example.demo.dto.AdmincheckStatusByBrandReportDto;
import com.example.demo.dto.AgentTicketDetailDto;
import com.example.demo.dto.FeedbackScoreSummaryDto;
import com.example.demo.dto.SlaComplianceReportDto;
import com.example.demo.dto.TicketVolumeByGroupDto;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.ComplaintStatus;
import com.example.demo.entity.Feedback;
import com.example.demo.entity.SupportAgent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsService {
    
    // ... existing repositories ...
    private final ComplaintRepository complaintRepository;
    private final FeedbackRepository feedbackRepository;

    // ... your existing getMerchantAnalytics method and its helpers ...

    // ✅ ADD THIS NEW METHOD FOR THE ADMIN
    public AdminAnalyticsSummaryDto getAdminAnalytics() {
        // Fetch all data needed
        List<Complaint> allComplaints = complaintRepository.findAll();
        List<Feedback> allFeedback = feedbackRepository.findAll();
        List<Complaint> closedComplaints = complaintRepository.findByStatusIn(
            List.of(ComplaintStatus.RESOLVED, ComplaintStatus.CLOSED)
        );

        // Calculate basic stats
        long totalTickets = allComplaints.size();
        long openTickets = complaintRepository.countByStatus(ComplaintStatus.OPEN);
        long resolvedToday = complaintRepository.countByStatusInAndUpdatedAtAfter(
            List.of(ComplaintStatus.RESOLVED, ComplaintStatus.CLOSED),
            LocalDate.now().atStartOfDay()
        );

        // Calculate detailed reports
       
        List<TicketVolumeByGroupDto> volumeByCategory = calculateVolumeBy(allComplaints, c -> c.getCategory() != null ? c.getCategory().name() : "null");
        // ✅ ADD THIS LINE to calculate volume by priority
        List<TicketVolumeByGroupDto> volumeByPriority = calculateVolumeBy(allComplaints, c -> c.getPriority() != null ? c.getPriority().name() : "null");        
        SlaComplianceReportDto slaReport = calculateGlobalSlaCompliance(closedComplaints);
        List<AdminPerformanceReportDto> agentPerformance = calculateAgentPerformance(closedComplaints);
        FeedbackScoreSummaryDto feedbackSummary = calculateGlobalFeedbackScores(allFeedback);

        // ✅ UPDATE THE RETURN STATEMENT to include the new data
     // ✅ UPDATE THE RETURN STATEMENT to include the new data
        return new AdminAnalyticsSummaryDto(
                totalTickets, 
                openTickets, 
                resolvedToday, 
                volumeByCategory, 
                volumeByPriority, 
                slaReport, 
                agentPerformance, 
                feedbackSummary
            );
    }
    
    // --- New Private Helpers for Admin Analytics ---

    private List<TicketVolumeByGroupDto> calculateVolumeBy(List<Complaint> complaints, Function<Complaint, String> classifier) {
        return complaints.stream()
                .collect(Collectors.groupingBy(classifier, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new TicketVolumeByGroupDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    // ✅ LOGIC ADDED
    private SlaComplianceReportDto calculateGlobalSlaCompliance(List<Complaint> closedComplaints) {
        if (closedComplaints.isEmpty()) {
            return new SlaComplianceReportDto(0, 0, 100.0);
        }
        long slaMetCount = closedComplaints.stream()
            .filter(c -> !c.getUpdatedAt().isAfter(c.getSlaEndTime()))
            .count();
        long slaMissedCount = closedComplaints.size() - slaMetCount;
        double complianceRate = ((double) slaMetCount / closedComplaints.size()) * 100.0;
        return new SlaComplianceReportDto(slaMetCount, slaMissedCount, Math.round(complianceRate * 100.0) / 100.0);
    }

    // ✅ LOGIC ADDED
    private FeedbackScoreSummaryDto calculateGlobalFeedbackScores(List<Feedback> allFeedback) {
        if (allFeedback.isEmpty()) {
            return new FeedbackScoreSummaryDto(0.0, 0);
        }
        double avgScore = allFeedback.stream()
            .mapToInt(Feedback::getRating)
            .average()
            .orElse(0.0);
        return new FeedbackScoreSummaryDto(Math.round(avgScore * 10.0) / 10.0, allFeedback.size());
    }
    
    
    private List<AdminPerformanceReportDto> calculateAgentPerformance(List<Complaint> allClosedComplaints) {
        // Group all closed complaints by the assigned support agent
        Map<SupportAgent, List<Complaint>> complaintsByAgent = allClosedComplaints.stream()
            .filter(c -> c.getSupportAgent() != null)
            .collect(Collectors.groupingBy(Complaint::getSupportAgent));

        // Calculate stats for each agent
        return complaintsByAgent.entrySet().stream()
            .map(entry -> {
                SupportAgent agent = entry.getKey();
                List<Complaint> agentComplaints = entry.getValue();
                long resolvedCount = agentComplaints.size();

                // ✅ START: Create the list of ticket details
                List<AgentTicketDetailDto> ticketDetails = agentComplaints.stream()
                    .map(complaint -> new AgentTicketDetailDto(
                        complaint.getTicketId(),
                        complaint.getPriority() != null ? complaint.getPriority().name() : "UNKNOWN",
                        complaint.getStatus() != null ? complaint.getStatus().name() : "UNKNOWN"
                    ))
                    .collect(Collectors.toList());
                // ✅ END: Create the list of ticket details

                long totalSeconds = agentComplaints.stream()
                    .mapToLong(c -> Duration.between(c.getCreatedAt(), c.getUpdatedAt()).getSeconds())
                    .sum();
                
                double avgHours = (resolvedCount > 0) ? (totalSeconds / (double) resolvedCount) / 3600.0 : 0.0;

                return new AdminPerformanceReportDto(
                    agent.getId(),
                    agent.getName(),
                    resolvedCount,
                    Math.round(avgHours * 10.0) / 10.0,
                    ticketDetails // ✅ Pass the new list here
                );
            })
            .collect(Collectors.toList());
    }
    
    
    public List<AdmincheckStatusByBrandReportDto> getTicketStatusByBrand() {
        // 1. Fetch all complaints from the database
        List<Complaint> allComplaints = complaintRepository.findAll();

        // 2. Group all complaints first by Brand Name, then by Status, and count them
        Map<String, Map<String, Long>> countsByBrandAndStatus = allComplaints.stream()
            .filter(c -> c.getBrandName() != null && !c.getBrandName().isEmpty()) // Ignore complaints with no brand
            .collect(Collectors.groupingBy(
                Complaint::getBrandName, // First, group by brand name
                Collectors.groupingBy(c -> c.getStatus().name(), Collectors.counting()) // Then, group by status and count
            ));

        // 3. Convert the complex map into a clean list of DTOs for the response
        return countsByBrandAndStatus.entrySet().stream()
            .map(entry -> {
                String brandName = entry.getKey();
                Map<String, Long> statusCounts = entry.getValue();
                long totalCount = statusCounts.values().stream().mapToLong(Long::valueOf).sum();
                return new AdmincheckStatusByBrandReportDto(brandName, totalCount, statusCounts);
            })
            .collect(Collectors.toList());
    }
    
    
}