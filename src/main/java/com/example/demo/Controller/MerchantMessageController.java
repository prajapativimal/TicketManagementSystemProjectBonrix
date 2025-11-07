package com.example.demo.Controller;


import com.example.demo.Service.MessageService;
import com.example.demo.Service.TicketChatService;
import com.example.demo.dto.ConversationViewResponse;
import com.example.demo.dto.SendMessageRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import com.example.demo.dto.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class MerchantMessageController {
    private final TicketChatService ticketChatService;
   
    @PostMapping("/api/merchant/complaints/{ticketId}/messages")
    @PreAuthorize("hasAuthority('MERCHANT')")
    // ✅ Change @RequestBody to @ModelAttribute
    public ResponseEntity<String> postMessage(
            @PathVariable String ticketId,
            @ModelAttribute SendMessageRequest request, 
            Authentication auth) throws Exception {
        
        // ✅ The service call now passes the whole request object
        ticketChatService.sendMessageByMerchant(ticketId, request, auth.getName());
        return ResponseEntity.ok("Message sent.");
    }
    @GetMapping("/api/complaints/{ticketId}/messages")
    public ResponseEntity<List<ConversationViewResponse>> getMessages(@PathVariable String ticketId, Authentication auth) {
        return ResponseEntity.ok(ticketChatService.getMessagesForTicket(ticketId, auth));
    }
    
}