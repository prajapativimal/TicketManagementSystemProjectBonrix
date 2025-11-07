package com.example.demo.Service;



import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.Repository.MerchantRepository;
import com.example.demo.Repository.MessageRepository;
import com.example.demo.dto.MessageResponse;
import com.example.demo.dto.SendMessageRequest;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.Merchant;
import com.example.demo.entity.Message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ComplaintRepository complaintRepository;
    private final MerchantRepository merchantRepository;

    // Logic for Merchant to send a message
    public void sendMessageByMerchant(String ticketId, SendMessageRequest request, String merchantContactNumber) {
        Complaint complaint = complaintRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));
        
        Merchant merchant = merchantRepository.findByContactNumber(merchantContactNumber)
                .orElseThrow(() -> new RuntimeException("Merchant not found: " + merchantContactNumber));

        // Security Check: Make sure the complaint belongs to this merchant
        if (!complaint.getMerchantId().equals(merchant.getId())) {
            throw new SecurityException("You do not have permission to comment on this ticket.");
        }

        Message message = Message.builder()
                .complaint(complaint)
                .merchant(merchant)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        
        messageRepository.save(message);
    }

    // Logic for Admin to get all messages for a ticket
    public List<MessageResponse> getMessagesForTicketByAdmin(String ticketId) {
        Complaint complaint = complaintRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

        List<Message> messages = messageRepository.findByComplaintOrderByCreatedAtAsc(complaint);
        
        // Convert entities to DTOs for the response
        return messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
 // ✅ ADD THIS NEW METHOD
    public List<MessageResponse> getAllMessagesByAdmin() {
        List<Message> messages = messageRepository.findAllByOrderByCreatedAtDesc();
        
        // Convert the list of entities to a list of DTOs
        return messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
        

 // ✅ UPDATE THIS HELPER METHOD to include the ticketId
    private MessageResponse convertToResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getCreatedAt(),
                message.getMerchant().getId(),
                message.getMerchant().getMerchantName(),
                message.getComplaint().getTicketId() // Get the ticketId from the complaint
        );
    }
}