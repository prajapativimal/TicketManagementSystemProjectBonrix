package com.example.demo.Controller;

import com.example.demo.Service.TicketChatService;
import com.example.demo.dto.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SubAdminBrandMessageController
{
    private final TicketChatService ticketChatService;
    @PostMapping("/api/subadminbrandmessage/complaints/{ticketId}/messages")
    @PreAuthorize("hasAuthority('BRAND_ADMIN')")
    public ResponseEntity<String> postMessage(
            @PathVariable String ticketId,
            @ModelAttribute SendMessageRequest request,
            Authentication auth) throws Exception {
        
        // auth.getName() will return the brand admin's email from the JWT
        ticketChatService.sendMessageByBrandAdmin(ticketId, request, auth.getName());
        return ResponseEntity.ok("Message sent.");
    }
    
    
    

}
