package com.example.demo.Controller;

import com.example.demo.Service.ComplaintService;
import com.example.demo.Service.SupportAgentAuthService;
import com.example.demo.dto.*;
import com.example.demo.entity.Complaint;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication; // Add this import

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/support-agent")
@RequiredArgsConstructor
public class SupportAgentAuthController {

    private final SupportAgentAuthService supportAgentAuthService;
    private final ComplaintService complaintService; // ✅ Inject ComplaintService


    // ✅ Public - Register Support Agent
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody SupportAgentRegisterRequest request) {
        return ResponseEntity.ok(supportAgentAuthService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SupportAgentLoginRequest request) {
        // ✅ Add a try-catch block to handle the specific error
        try {
            // This is the success path
            AuthResponse response = supportAgentAuthService.login(request);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            // ✅ This catches the "deactivated account" error from your service
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage()); // e.g., "This support agent account has been deactivated."
            errorResponse.put("status", false);
            
            // Return the custom error with a 403 Forbidden status
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
    }

    // ✅ Protected - Only SUPPORT_AGENT can access
    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Welcome Support Agent! You have access to this dashboard.");
    }
    
    @GetMapping("/my-complaints")
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
//                dto.setOrderId(c.getOrderId());
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
    //status will be changes open to inprocess 
    
    
   /* @GetMapping("/update-status/{ticketId}")
    @PreAuthorize("hasRole('SUPPORT_AGENT')")
    public ResponseEntity<?> updateComplaintStatusByTicketId(
            @PathVariable String ticketId,
            Authentication authentication) {

        String agentEmail = authentication.getName();
        String message = complaintService.updateStatusByTicketId(agentEmail, ticketId);
        return ResponseEntity.ok(Map.of("message", message));
    }*/


    
    
 // ✅ Change complaint status
    @PutMapping("/complaints/{id}/status")
    @PreAuthorize("hasRole('SUPPORT_AGENT')")
    public ResponseEntity<Complaint> updateStatus(
            @PathVariable Long id,
            @RequestBody SupportUpdateStatusRequest request
    ) {
        Complaint updated = complaintService.updateComplaintStatus(id, request.getStatus());
        return ResponseEntity.ok(updated);
    }
}
