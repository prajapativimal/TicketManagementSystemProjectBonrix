package com.example.demo.Controller;


import com.example.demo.Service.ComplaintService;
import com.example.demo.Service.TicketChatService;
import com.example.demo.dto.ComplaintResponse;
import com.example.demo.dto.SendMessageRequest;
import com.example.demo.entity.Complaint;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class SupportAgentMessageController {
    private final TicketChatService ticketChatService;

    
    @PostMapping("/api/support-agent/complaints/{ticketId}/messages")
    @PreAuthorize("hasAuthority('SUPPORT_AGENT')")
    public ResponseEntity<String> postMessage(
            @PathVariable String ticketId,
            @ModelAttribute SendMessageRequest request, // ✅ Change to @ModelAttribute
            Authentication auth) throws Exception {
        
        // ✅ The service call now passes the whole request object
        ticketChatService.sendMessageBySupportAgent(ticketId, request, auth.getName());
        return ResponseEntity.ok("Message sent.");
    }
    
    
    
  
}