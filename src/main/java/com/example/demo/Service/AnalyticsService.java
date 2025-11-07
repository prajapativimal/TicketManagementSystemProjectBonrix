package com.example.demo.Service;


import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.Repository.FeedbackRepository;
import com.example.demo.Repository.MerchantRepository;
import com.example.demo.dto.*;
import com.example.demo.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ComplaintRepository complaintRepository;
    private final FeedbackRepository feedbackRepository;
    private final MerchantRepository merchantRepository;

    public AnalyticsSummaryResponseDto getMerchantAnalytics(String merchantContactNumber) {
        Merchant merchant = merchantRepository.findByContactNumber(merchantContactNumber).orElseThrow();
        List<Complaint> allComplaints = complaintRepository.findByMerchantId(merchant.getId());
        List<Feedback> allFeedback = feedbackRepository.findByMerchant(merchant);

        // ✅ START: ADD NULL CHECKS HERE
        List<TicketVolumeByGroupDto> volumeByCategory = calculateVolumeBy(allComplaints, 
            c -> c.getCategoryName() != null ? c.getCategoryName() : "UNKNOWN"
        );
        List<TicketVolumeByGroupDto> volumeByStatus = calculateVolumeBy(allComplaints, 
            c -> c.getStatus() != null ? c.getStatus().name() : "UNKNOWN"
        );
        List<TicketVolumeByGroupDto> volumeByPriority = calculateVolumeBy(allComplaints, 
            c -> c.getPriority() != null ? c.getPriority().name() : "UNKNOWN"
        );
        // ✅ END: ADD NULL CHECKS HERE
        
        SlaComplianceReportDto slaReport = calculateSlaCompliance(allComplaints);
        AverageResolutionTimeDto resolutionTime = calculateAverageResolutionTime(allComplaints);
        FeedbackScoreSummaryDto feedbackSummary = calculateFeedbackScores(allFeedback);

        return new AnalyticsSummaryResponseDto(volumeByCategory, volumeByStatus, volumeByPriority, slaReport, resolutionTime, feedbackSummary);
    }

    private List<TicketVolumeByGroupDto> calculateVolumeBy(List<Complaint> complaints, java.util.function.Function<Complaint, String> classifier) {
        return complaints.stream()
                .collect(Collectors.groupingBy(classifier, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new TicketVolumeByGroupDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private SlaComplianceReportDto calculateSlaCompliance(List<Complaint> complaints) {
        List<Complaint> closed = complaints.stream()
            .filter(c -> c.getStatus() == ComplaintStatus.RESOLVED || c.getStatus() == ComplaintStatus.CLOSED)
            .toList();
        // ... (logic is the same as the previous answer, just uses the 'complaints' list)
		return null;
    }

    private AverageResolutionTimeDto calculateAverageResolutionTime(List<Complaint> complaints) {
		return null;
        // ... (logic is the same as the previous answer, just uses the 'complaints' list)
    }

    private FeedbackScoreSummaryDto calculateFeedbackScores(List<Feedback> feedbacks) {
        if (feedbacks.isEmpty()) {
            return new FeedbackScoreSummaryDto(0.0, 0);
        }
        double avgScore = feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(0.0);
        return new FeedbackScoreSummaryDto(Math.round(avgScore * 10.0) / 10.0, feedbacks.size());
    }
    
    
    //
    // ✅ New method for report
    public List<TicketReportResponseDto> getMerchantReport(String merchantContactNumber) {
        Merchant merchant = merchantRepository.findByContactNumber(merchantContactNumber)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        List<Complaint> complaints = complaintRepository.findByMerchantId(merchant.getId());

        return complaints.stream().map(c -> {
            TicketReportResponseDto dto = new TicketReportResponseDto();
            dto.setTicketId(c.getTicketId());
            dto.setCategoryName(c.getCategoryName());
            dto.setBrandName(c.getBrandName());
            dto.setPriority(c.getPriority() != null ? c.getPriority().name() : "UNKNOWN");
            dto.setStatus(c.getStatus() != null ? c.getStatus().name() : "UNKNOWN");
            dto.setDescription(c.getDescription());
            dto.setCreatedAt(c.getCreatedAt());
            dto.setUpdatedAt(c.getUpdatedAt());
            dto.setSlaEndTime(c.getSlaEndTime());
            dto.setCity(c.getCity());
            dto.setState(c.getState());
            dto.setPincode(c.getPincode());
            dto.setModelNumber(c.getModelNumber());
            dto.setSerialNumber(c.getSerialNumber());
            dto.setOrderId(c.getOrderId());
            dto.setTransactionId(c.getTransactionId());
            dto.setStoreId(c.getStoreId());
            return dto;
        }).collect(Collectors.toList());
    }
    
    
}