package com.example.demo.Controller;

import com.example.demo.Service.TicketChatService;
import com.example.demo.dto.UnseenMessageCountResponse;
import com.example.demo.dto.DashboardUnseenCountResponse;
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
public class UnseenMessageController {
    
    private final TicketChatService ticketChatService;

    @GetMapping("/api/complaints/{ticketId}/unseen-count")
    @PreAuthorize("hasAnyAuthority('MERCHANT', 'ADMIN', 'SUPPORT_AGENT', 'BRAND_ADMIN')")
    public ResponseEntity<UnseenMessageCountResponse> getUnseenMessageCount(
            @PathVariable String ticketId,
            Authentication auth) {
        
        UnseenMessageCountResponse response = ticketChatService.getUnseenMessageCount(ticketId, auth);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/complaints/{ticketId}/mark-as-seen")
    @PreAuthorize("hasAnyAuthority('MERCHANT', 'ADMIN', 'SUPPORT_AGENT', 'BRAND_ADMIN')")
    public ResponseEntity<String> markMessagesAsSeen(
            @PathVariable String ticketId,
            Authentication auth) {
        
        ticketChatService.markMessagesAsSeen(ticketId, auth);
        return ResponseEntity.ok("Messages marked as seen");
    }

    @GetMapping("/api/dashboard/unseen-messages")
    @PreAuthorize("hasAnyAuthority('MERCHANT', 'ADMIN', 'SUPPORT_AGENT', 'BRAND_ADMIN')")
    public ResponseEntity<DashboardUnseenCountResponse> getDashboardUnseenCount(Authentication auth) {
        DashboardUnseenCountResponse response = ticketChatService.getDashboardUnseenCount(auth);
        return ResponseEntity.ok(response);
    }
}
