package com.example.demo.Controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.ComplaintService;
import com.example.demo.Service.SubAdminBrandAuthService;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.ComplaintResponse;
import com.example.demo.dto.SubAdminBrandLoginRequest;
import com.example.demo.dto.SubAdminUpdateStatusRequest;
import com.example.demo.entity.Complaint;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/SubAdminbrand")
@RequiredArgsConstructor
public class SubAdminBrandAuthController 
{
	private final SubAdminBrandAuthService  subadminauthService;
    private final ComplaintService complaintService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody SubAdminBrandLoginRequest request) {
        return ResponseEntity.ok(subadminauthService.login(request));
    }
    
    
    @GetMapping("/complaints")
    public ResponseEntity<List<ComplaintResponse>> getBrandComplaints(Principal principal) {
        String brandContactNumber = principal.getName();
        List<Complaint> complaints = complaintService.getComplaintsForBrand(brandContactNumber);

        // ✅ THE FIX IS HERE: This mapping logic must be complete
        List<ComplaintResponse> responseList = complaints.stream()
            .map(c -> {
                ComplaintResponse dto = new ComplaintResponse();

                // === COPY ALL DATA FROM THE ENTITY (c) TO THE DTO (dto) ===
                dto.setId(c.getId());
                dto.setTicketId(c.getTicketId());
                dto.setMerchantName(c.getMerchantName());
                dto.setDescription(c.getDescription());
                dto.setStatus(c.getStatus() != null ? c.getStatus().name() : null);
                dto.setPriority(c.getPriority() != null ? c.getPriority().name() : null);
                dto.setCategory(c.getCategory() != null ? c.getCategory().name() : null);
                dto.setCreatedAt(c.getCreatedAt());
                dto.setUpdatedAt(c.getUpdatedAt());
                dto.setSlaEndTime(c.getSlaEndTime());
                dto.setDeviceOrderId(c.getDeviceOrderId());
                dto.setSerialNumber(c.getSerialNumber());
                dto.setTransactionId(c.getTransactionId());
                dto.setStoreId(c.getStoreId());
                dto.setAddress(c.getAddress());
                dto.setContactNumber(c.getContactNumber());
                dto.setCity(c.getCity());
                dto.setState(c.getState());
                dto.setPincode(c.getPincode());
                dto.setCategoryName(c.getCategoryName());
                dto.setIssues(c.getIssueName());
                dto.setModelNumber(c.getModelNumber());
                dto.setBrandName(c.getBrandName());
                
                // --- Build attachment URLs ---
                List<String> urls = Collections.emptyList();
                String attachments = c.getAttachments();
                if (attachments != null && !attachments.isEmpty()) {
                    urls = Arrays.stream(attachments.split(","))
                            .map(fileName -> "/images/" + c.getTicketId() + "/" + fileName.trim())
                            .collect(Collectors.toList());
                }
                dto.setAttachmentUrls(urls);
                // ---------------------------------
                
                return dto;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
    
    // ✅ Access only to BRAND_ADMIN
    @PutMapping("/update-status")
    @PreAuthorize("hasRole('BRAND_ADMIN')")
    public ResponseEntity<?> updateComplaintStatusByBrandAdmin(
            @RequestBody SubAdminUpdateStatusRequest request) {

        String message = complaintService.updateComplaintStatusByBrandAdmin(
                request.getTicketId(),
                request.getNewStatus()
        );

        return ResponseEntity.ok(Map.of("message", message));

    }
    

}
