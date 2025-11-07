package com.example.demo.Controller;



import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.Service.AdminAnalyticsService;
import com.example.demo.Service.AnalyticsService;
import com.example.demo.dto.AdminAnalyticsSummaryDto;
import com.example.demo.dto.AdmincheckStatusByBrandReportDto;
import com.example.demo.dto.ComplaintResponse;
import com.example.demo.entity.Complaint;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final AdminAnalyticsService analyticsService;
    private final ComplaintRepository complaintRepo; // directly use repo


    @GetMapping("/analytics")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AdminAnalyticsSummaryDto> getAdminAnalytics() {
        AdminAnalyticsSummaryDto response = analyticsService.getAdminAnalytics();
        return ResponseEntity.ok(response);
    }
    
    
 // ✅ ADD THIS NEW ENDPOINT
    @GetMapping("/reports/status-by-brand")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<AdmincheckStatusByBrandReportDto>> getStatusByBrandReport() {
        List<AdmincheckStatusByBrandReportDto> response = analyticsService.getTicketStatusByBrand();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/reports/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints() {
        List<Complaint> complaints = complaintRepo.findAll();

        List<ComplaintResponse> responseList = complaints.stream().map(c -> {
            ComplaintResponse dto = new ComplaintResponse();
            dto.setId(c.getId());
            dto.setTicketId(c.getTicketId());
            dto.setMerchantName(c.getMerchantName());
            dto.setBrandName(c.getBrandName());

            dto.setCategory(c.getCategory() != null ? c.getCategory().name() : null);
            dto.setDescription(c.getDescription());
            dto.setDeviceOrderId(c.getDeviceOrderId());
            dto.setPriority(c.getPriority() != null ? c.getPriority().name() : null);
            dto.setStatus(c.getStatus() != null ? c.getStatus().name() : null);
            dto.setCreatedAt(c.getCreatedAt());
            dto.setUpdatedAt(c.getUpdatedAt());
            dto.setSlaEndTime(c.getSlaEndTime());
            
            // ✅ START: MAP ALL NEW FIELDS
            dto.setSerialNumber(c.getSerialNumber());
            dto.setTransactionId(c.getTransactionId());
//            dto.setOrderId(c.getOrderId());
            dto.setStoreId(c.getStoreId());
            dto.setAddress(c.getAddress());
            dto.setContactNumber(c.getContactNumber());
            dto.setCategoryName(c.getCategoryName());
            dto.setIssues(c.getIssueName());
            dto.setCity(c.getCity());
            dto.setState(c.getState());
            dto.setPincode(c.getPincode());
            dto.setModelNumber(c.getModelNumber());
            // ✅ END: MAP ALL NEW FIELDS


            // ✅ START: GET BOTH AGENT NAMES
            // Get the name for the 'supportAgent' relationship
            if (c.getSupportAgent() != null) {
                dto.setSupportAgentName(c.getSupportAgent().getName());
            } else {
                dto.setSupportAgentName(null);
            }

            // Get the name for the 'assignedAgent' relationship
            if (c.getAssignedAgent() != null) {
                dto.setAssignedAgentName(c.getAssignedAgent().getName());
            } else {
                dto.setAssignedAgentName(null);
            }
            // ✅ END: GET BOTH AGENT NAMES


            // Attachment URL logic remains the same
            List<String> urls = Collections.emptyList();
            String attachments = c.getAttachments();
            
            if (attachments != null && !attachments.isEmpty()) {
                urls = Arrays.stream(attachments.split(","))
                        .map(fileName -> "/images/" + c.getTicketId() + "/" + fileName.trim())
                        .collect(Collectors.toList());
            }
            dto.setAttachmentUrls(urls);

            return dto;
        }).toList();

        return ResponseEntity.ok(responseList);
    }
}