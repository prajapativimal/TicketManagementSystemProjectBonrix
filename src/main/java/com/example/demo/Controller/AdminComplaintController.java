package com.example.demo.Controller;

import com.example.demo.entity.Complaint;
import com.example.demo.entity.ComplaintStatus;
import com.example.demo.entity.Priority;
import com.example.demo.entity.SupportAgent;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.Service.FeedbackService;
import com.example.demo.Service.SupportAgentAuthService;
import com.example.demo.dto.ComplaintResponse;
import com.example.demo.dto.FeedbackResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/admin/complaints")
@RequiredArgsConstructor
public class AdminComplaintController {

    private final ComplaintRepository complaintRepo; // directly use repo
    private final SupportAgentAuthService supportAgentAuthService;
    private final FeedbackService feedbackService;

    
    // ✅ Get all complaints (Admin only)
    @GetMapping
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
    /* @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints() {
        List<Complaint> complaints = complaintRepo.findAll();

        // Map entities → DTO
       
        List<ComplaintResponse> responseList = complaints.stream().map(c -> {
            ComplaintResponse dto = new ComplaintResponse();
            dto.setId(c.getId());
            dto.setTicketId(c.getTicketId());
            dto.setMerchantName(c.getMerchantName());
            dto.setCategory(c.getCategory().name());        // assuming enum
            dto.setDescription(c.getDescription());
            dto.setPriority(c.getPriority().name());        // assuming enum
            dto.setStatus(c.getStatus().name());            // assuming enum
            dto.setCreatedAt(c.getCreatedAt());
            dto.setUpdatedAt(c.getUpdatedAt());
            dto.setSlaEndTime(c.getSlaEndTime());
            return dto;
        }).toList();


        return ResponseEntity.ok(responseList);
    }
    */
    //
 // Update complaint status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComplaintResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam("status") String statusStr) {

        Complaint complaint = complaintRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        ComplaintStatus newStatus = ComplaintStatus.valueOf(statusStr.toUpperCase()); // convert manually
        complaint.setStatus(newStatus);
        complaint.setUpdatedAt(LocalDateTime.now());

        complaintRepo.save(complaint);

        ComplaintResponse dto = new ComplaintResponse();
        dto.setId(complaint.getId());
        dto.setTicketId(complaint.getTicketId());
        dto.setMerchantName(complaint.getMerchantName());
        dto.setCategory(complaint.getCategory() != null ? complaint.getCategory().name() : null);
        dto.setDescription(complaint.getDescription());
        dto.setPriority(complaint.getPriority() != null ? complaint.getPriority().name() : null);
        dto.setStatus(complaint.getStatus() != null ? complaint.getStatus().name() : null);
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());
        dto.setSlaEndTime(complaint.getSlaEndTime());

        return ResponseEntity.ok(dto);
    }
    
    @PutMapping("/{id}/priority")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComplaintResponse> updatePriority(
            @PathVariable Long id,
            @RequestParam("priority") String priorityStr) {

        // 1. Find the complaint by its ID
        Complaint complaint = complaintRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        // 2. Convert the incoming string to the Priority enum
        Priority newPriority = Priority.valueOf(priorityStr.toUpperCase());
        
        // 3. Update the priority and the 'updatedAt' timestamp
        complaint.setPriority(newPriority);
        complaint.setUpdatedAt(LocalDateTime.now());

        // 4. Save the changes to the database
        complaintRepo.save(complaint);

        ComplaintResponse dto = new ComplaintResponse();
        dto.setId(complaint.getId());
        dto.setTicketId(complaint.getTicketId());
        dto.setMerchantName(complaint.getMerchantName());
        dto.setCategory(complaint.getCategory() != null ? complaint.getCategory().name() : null);
        dto.setDescription(complaint.getDescription());
        dto.setPriority(complaint.getPriority() != null ? complaint.getPriority().name() : null);
        dto.setStatus(complaint.getStatus() != null ? complaint.getStatus().name() : null);
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());
        dto.setSlaEndTime(complaint.getSlaEndTime());

        return ResponseEntity.ok(dto);
    }
    

//
 // ✅ Only ADMIN can access this API
    @GetMapping("/support-agents")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SupportAgent>> getAllSupportAgents() {
        return ResponseEntity.ok(supportAgentAuthService.getAllSupportAgents());
    }
    
    
    //admin get all feed back 
    
    @GetMapping("/feedback")
    @PreAuthorize("hasAuthority('ADMIN')") // 🔐 Secures the endpoint for admins only
    public ResponseEntity<List<FeedbackResponseDto>> viewAllFeedback() {
        List<FeedbackResponseDto> allFeedback = feedbackService.getAllFeedback();
        return ResponseEntity.ok(allFeedback);
    }
}
