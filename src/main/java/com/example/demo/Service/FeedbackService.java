// In your FeedbackService.java
package com.example.demo.Service;

import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.Repository.FeedbackRepository;
import com.example.demo.Repository.MerchantRepository;
import com.example.demo.dto.FeedbackRequest;
import com.example.demo.dto.FeedbackResponseDto;
import com.example.demo.dto.MerchantSummaryDto;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.ComplaintStatus;
import com.example.demo.entity.Feedback;
import com.example.demo.entity.Merchant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final MerchantRepository merchantRepository;
    private final FeedbackRepository feedbackRepository;
    private final ComplaintRepository complaintRepository; // ✅ 2. Inject the repository


    // The first parameter is now the contact number from the JWT
    public void saveFeedback(String merchantContactNumber, FeedbackRequest request) {
        
        // 1. Find the merchant by their contact number instead of email
        Merchant merchant = merchantRepository.findByContactNumber(merchantContactNumber)
                .orElseThrow(() -> new RuntimeException("Merchant not found with contact number: " + merchantContactNumber));

        // ✅ 2. ADD VALIDATION LOGIC only feedback for RESOLVED RESOLVED
        // Find the complaint the feedback is for
        Complaint complaint = complaintRepository.findByTicketId(request.ticketId())
                .orElseThrow(() -> new RuntimeException("Complaint with ticket ID " + request.ticketId() + " not found."));
        
        // Security check: ensure the merchant owns this complaint
        if (!complaint.getMerchantId().equals(merchant.getId())) {
            throw new SecurityException("You are not authorized to provide feedback for this ticket.");
        }

        // Status check: only allow feedback for resolved or closed tickets
        if (complaint.getStatus() == ComplaintStatus.OPEN || complaint.getStatus() == ComplaintStatus.IN_PROGRESS) {
            throw new IllegalStateException("Feedback can only be submitted for RESOLVED or CLOSED tickets.Contact Admin");
        }
        
        
        // ✅ ADD THIS CHECK to see if feedback already exists
        boolean feedbackExists = feedbackRepository.existsByTicketId(request.ticketId());
        if (feedbackExists) {
            throw new IllegalStateException("Feedback has already been submitted for this ticket.");
        }
        
        // 3. Build the new Feedback entity (this part remains the same)
        Feedback feedback = Feedback.builder()
                .merchant(merchant)
                .rating(request.rating())
                .comment(request.comment())
                .ticketId(request.ticketId())
                .createdAt(LocalDateTime.now())
                .build();

        // 3. Save the feedback to the database
        feedbackRepository.save(feedback);
    }
    //admin get feed back logic 
 // ✅ ADD THIS NEW METHOD FOR THE ADMIN
    public List<FeedbackResponseDto> getAllFeedback() {
        // 1. Fetch all feedback records from the database
        List<Feedback> allFeedback = feedbackRepository.findAll();

        // 2. Convert the list of entities to a list of DTOs
        return allFeedback.stream()
                .map(this::convertToFeedbackResponseDto)
                .collect(Collectors.toList());
    }
    
    //merchant get feedback for current user wise logic
    // ✅ ADD THIS NEW METHOD FOR A SINGLE MERCHANT
    public List<FeedbackResponseDto> getFeedbackForMerchant(String merchantContactNumber) {
        // 1. Find the merchant by their contact number from the JWT
        Merchant merchant = merchantRepository.findByContactNumber(merchantContactNumber)
                .orElseThrow(() -> new RuntimeException("Merchant not found with contact number: " + merchantContactNumber));

        // 2. Use the new repository method to get feedback for only that merchant
        List<Feedback> feedbackList = feedbackRepository.findByMerchant(merchant);

        // 3. Convert the list of entities to DTOs (reusing the same helper method)
        return feedbackList.stream()
                .map(this::convertToFeedbackResponseDto)
                .collect(Collectors.toList());
    }

    // ✅ Add this private helper method for conversion
    private FeedbackResponseDto convertToFeedbackResponseDto(Feedback feedback) {
        MerchantSummaryDto merchantDto = new MerchantSummaryDto(
                feedback.getMerchant().getId(),
                feedback.getMerchant().getMerchantName(),
                feedback.getMerchant().getContactNumber()
        );

        return new FeedbackResponseDto(
                feedback.getId(),
                feedback.getRating(),
                feedback.getComment(),
                feedback.getTicketId(),
                feedback.getCreatedAt(),
                merchantDto
        );
    }
}