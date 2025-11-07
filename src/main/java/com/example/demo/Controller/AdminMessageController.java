package com.example.demo.Controller;




import com.example.demo.Service.TicketChatService;
import com.example.demo.dto.SendMessageRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class AdminMessageController {
    private final TicketChatService ticketChatService;
    @PostMapping("/api/admin/complaints/{ticketId}/messages")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> postMessage(
            @PathVariable String ticketId,
            @ModelAttribute SendMessageRequest request, // ✅ Change to @ModelAttribute
            Authentication auth) throws Exception {
        
        // ✅ The service call now passes the whole request object
        ticketChatService.sendMessageByAdmin(ticketId, request, auth.getName());
        return ResponseEntity.ok("Message sent.");
    }
}