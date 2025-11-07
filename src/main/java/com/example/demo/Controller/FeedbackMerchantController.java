// In your FeedbackMerchantController.java
package com.example.demo.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // Correct import
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.Service.FeedbackService;
import com.example.demo.dto.FeedbackRequest;
import com.example.demo.dto.FeedbackResponseDto;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/feedbackmerchant") // Changed path for consistency
@RequiredArgsConstructor
public class FeedbackMerchantController {

    private final FeedbackService feedbackService;

    @PostMapping("/feedback")
    @PreAuthorize("hasAuthority('MERCHANT')")
    public ResponseEntity<?> submitFeedback(
            @RequestBody FeedbackRequest feedbackRequest,
            Authentication authentication) {

        // ✅ START: Add try-catch block
        try {
            String merchantContactNumber = authentication.getName();
            feedbackService.saveFeedback(merchantContactNumber, feedbackRequest);
            return ResponseEntity.ok("Thank you for your feedback!");

        } catch (SecurityException e) {
            // ✅ This catches the "not authorized" error
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            // 403 Forbidden is the correct status for authorization issues
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);

        } catch (RuntimeException e) {
            // ✅ This catches other validation errors like "ticket not resolved"
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            // 400 Bad Request is for invalid requests
            return ResponseEntity.ok(errorResponse); // Changed from .status(HttpStatus.BAD_REQUEST)
        }
        // ✅ END: Add try-catch block
    }
    
    //get feedback current user wise 
 // ✅ ADD THIS NEW ENDPOINT
    @GetMapping("/my-feedback")
    @PreAuthorize("hasAuthority('MERCHANT')") // 🔐 Secures the endpoint for merchants only
    public ResponseEntity<List<FeedbackResponseDto>> getMyFeedbackHistory(Authentication authentication) {
        // Get the logged-in merchant's contact number from their JWT
        String merchantContactNumber = authentication.getName();
        
        List<FeedbackResponseDto> feedbackHistory = feedbackService.getFeedbackForMerchant(merchantContactNumber);
        
        return ResponseEntity.ok(feedbackHistory);
    }
}