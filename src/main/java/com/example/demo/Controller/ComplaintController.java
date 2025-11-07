
package com.example.demo.Controller;




import com.example.demo.Service.ComplaintService;
import com.example.demo.dto.ComplaintRequest;
import com.example.demo.dto.ComplaintResponse;
import com.example.demo.dto.MerchantReopenRequest;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.DeviceModelNumber;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    // Submit complaint -> Merchant only
    @PostMapping("/complaint")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<?> submitComplaint(
            Principal principal,
            @ModelAttribute ComplaintRequest request
    ) {
        // ✅ START: Add try-catch block
        try {
            // This is the success path
            Complaint complaint = complaintService.submitComplaint(principal.getName(), request);
            return ResponseEntity.ok(complaint);
            
        } catch (RuntimeException e) {
            // This is the error path. It catches the validation exception.
            // Create a simple map to hold the error message.
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            
            // Return the map with a 400 Bad Request status
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
             // Catch any other unexpected exceptions
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
        // ✅ END: Add try-catch block
    }

    @GetMapping("/complaints")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<List<ComplaintResponse>> getComplaints(Principal principal) {
        // The service call is correct, it returns a List<Complaint>
        List<Complaint> complaints = complaintService.getMerchantComplaints(principal.getName());

        List<ComplaintResponse> responseList = complaints.stream()
            .map(c -> {
                ComplaintResponse dto = new ComplaintResponse();
                // --- Existing Mappings (Correct) ---
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
                dto.setSerialNumber(c.getSerialNumber());
                dto.setTransactionId(c.getTransactionId());
//                dto.setOrderId(c.getOrderId());
                dto.setStoreId(c.getStoreId());
                dto.setAddress(c.getAddress());
                dto.setContactNumber(c.getContactNumber());

                // ✅ ADD MAPPING FOR CITY, STATE, AND PINCODE
                dto.setCity(c.getCity());
                dto.setState(c.getState());
                dto.setPincode(c.getPincode());
                dto.setCategoryName(c.getCategoryName());
                dto.setIssues(c.getIssueName());
                dto.setModelNumber(c.getModelNumber());                // ✅ ADD MAPPING FOR AGENT NAMES (This was also missing)
                if (c.getSupportAgent() != null) {
                    dto.setSupportAgentName(c.getSupportAgent().getName());
                }
                if (c.getAssignedAgent() != null) {
                    dto.setAssignedAgentName(c.getAssignedAgent().getName());
                }

                // --- Attachment URL Logic (Correct) ---
                List<String> urls = Collections.emptyList();
                String attachments = c.getAttachments();
                
                if (attachments != null && !attachments.isEmpty()) {
                    urls = Arrays.stream(attachments.split(","))
                            .map(fileName -> "/images/" + c.getTicketId() + "/" + fileName.trim())
                            .collect(Collectors.toList());
                }
                dto.setAttachmentUrls(urls);
                
                return dto;
            })
            .toList();

        return ResponseEntity.ok(responseList);
    }
    
    @PutMapping("/reopen")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<?> reopenComplaintByMerchant(
            @RequestBody MerchantReopenRequest request,
            Authentication authentication) {

        String merchantEmailOrPhone = authentication.getName();
        String message = complaintService.reopenComplaintByMerchant(
                merchantEmailOrPhone,
                request.getTicketId()
        );

        return ResponseEntity.ok(Map.of("message", message));
    }

    


@GetMapping("/device-models")
@PreAuthorize("hasRole('MERCHANT')")
public ResponseEntity<List<DeviceModelNumber>> getAllModels() {
    return ResponseEntity.ok(complaintService.getAllModels());
}

}


/*package com.example.demo.Controller;

import com.example.demo.Service.ComplaintService;
import com.example.demo.dto.ComplaintRequest;
import com.example.demo.dto.ComplaintResponse;
import com.example.demo.entity.Complaint;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    // A list of allowed file content types (MIME types)
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
        "image/jpeg", 
        "image/png", 
        "image/gif", 
        "application/pdf", 
        "application/msword", // for .doc
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // for .docx
    );

    // Submit complaint -> Merchant only
    @PostMapping("/complaint")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<?> submitComplaint(
            Principal principal,
            @ModelAttribute ComplaintRequest request
    ) throws Exception {
        
        // --- ADDED VALIDATION ---
        // Check if attachments exist before validating them
        if (request.getAttachments() != null) {
            for (MultipartFile file : request.getAttachments()) {
                // Ensure the file is not empty
                if (file.isEmpty()) {
                    return ResponseEntity.badRequest().body("Cannot upload empty files.");
                }
                // Check if the file type is in our allowed list
                String contentType = file.getContentType();
                if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType)) {
                    return ResponseEntity.badRequest().body("File type not allowed: " + contentType);
                }
            }
        }
        // --- END OF VALIDATION ---

        Complaint complaint = complaintService.submitComplaint(principal.getName(), request);
        return ResponseEntity.ok(complaint);
    }

    @GetMapping("/complaints")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<List<ComplaintResponse>> getComplaints(Principal principal) {
        List<ComplaintResponse> responseList = complaintService.getMerchantComplaints(principal.getName())
            .stream()
            .map(c -> {
                ComplaintResponse dto = new ComplaintResponse();
                dto.setId(c.getId());
                dto.setTicketId(c.getTicketId());
                dto.setMerchantName(c.getMerchantName());
                dto.setCategory(c.getCategory().name());
                dto.setDescription(c.getDescription());
                dto.setDeviceOrderId(c.getDeviceOrderId());
                dto.setPriority(c.getPriority().name());
                dto.setStatus(c.getStatus().name());
                dto.setCreatedAt(c.getCreatedAt());
                dto.setUpdatedAt(c.getUpdatedAt());
                dto.setSlaEndTime(c.getSlaEndTime());
                return dto;
            })
            .toList();

        return ResponseEntity.ok(responseList);
    }
}
*/


