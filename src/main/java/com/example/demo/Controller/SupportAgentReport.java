package com.example.demo.Controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.ComplaintService;
import com.example.demo.Service.TicketChatService;
import com.example.demo.dto.ComplaintResponse;
import com.example.demo.entity.Complaint;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;


@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/support-agent/report")
@RequiredArgsConstructor
public class SupportAgentReport 
{
	
    private final ComplaintService complaintService; // ✅ Inject ComplaintService

	  @GetMapping
	    @PreAuthorize("hasAuthority('SUPPORT_AGENT')")
	    // ✅ 1. Change the return type to List<ComplaintResponse>
	    public ResponseEntity<List<ComplaintResponse>> getMyAssignedComplaints(Authentication authentication) {
	        String agentEmail = authentication.getName();
	        
	        // This part stays the same: fetch the raw entities from the service
	        List<Complaint> complaints = complaintService.getComplaintsForAgent(agentEmail);

	        // ✅ 2. Add the logic to map entities to DTOs
	        List<ComplaintResponse> responseList = complaints.stream()
	            .map(c -> {
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
	                //
	                
	                
	                dto.setSerialNumber(c.getSerialNumber());
	                dto.setTransactionId(c.getTransactionId());
//	                dto.setOrderId(c.getOrderId());
	                dto.setStoreId(c.getStoreId());
	                dto.setAddress(c.getAddress());
	                dto.setContactNumber(c.getContactNumber());
	                dto.setCategoryName(c.getCategoryName());
	                dto.setIssues(c.getIssueName());
	                dto.setCity(c.getCity());
	                dto.setState(c.getState());
	                dto.setPincode(c.getPincode());
	                dto.setModelNumber(c.getModelNumber());

	                // --- Build the attachment URLs ---
	                List<String> urls = Collections.emptyList();
	                String attachments = c.getAttachments();
	                
	                if (attachments != null && !attachments.isEmpty()) {
	                    urls = Arrays.stream(attachments.split(","))
	                            .map(fileName -> "/images/" + c.getTicketId() + "/" + fileName.trim())
	                            .collect(Collectors.toList());
	                }
	                dto.setAttachmentUrls(urls);
	                // --------------------------------

	                return dto;
	            })
	            .collect(Collectors.toList());

	        // ✅ 3. Return the new list of DTOs
	        return ResponseEntity.ok(responseList);
	    }

}
