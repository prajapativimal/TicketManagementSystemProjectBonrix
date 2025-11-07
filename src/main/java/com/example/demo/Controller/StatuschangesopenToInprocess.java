package com.example.demo.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.ComplaintService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/admin-support/status-change")
@RequiredArgsConstructor
public class StatuschangesopenToInprocess {

    private final ComplaintService complaintService;

    @GetMapping("/update-status/{ticketId}")
    @PreAuthorize("hasAnyRole('SUPPORT_AGENT', 'ADMIN')")
    public ResponseEntity<?> updateComplaintStatusByTicketId(
            @PathVariable String ticketId,
            Authentication authentication) {

        String agentEmail = authentication.getName();
        String message = complaintService.updateStatusByTicketId(agentEmail, ticketId);
        return ResponseEntity.ok(Map.of("status", 200, "message", message));
    }
}
