package com.example.demo.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.AnalyticsService;
import com.example.demo.dto.AnalyticsSummaryResponseDto;
import com.example.demo.dto.TicketReportResponseDto;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

//... imports ...

@RestController
@RequestMapping("/api/merchant")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class AnalyticsController {

 private final AnalyticsService analyticsService;

 @GetMapping("/analytics")
 @PreAuthorize("hasAuthority('MERCHANT')")
 public ResponseEntity<AnalyticsSummaryResponseDto> getAnalytics(Authentication authentication) {
     String merchantContact = authentication.getName();
     AnalyticsSummaryResponseDto response = analyticsService.getMerchantAnalytics(merchantContact);
     return ResponseEntity.ok(response);
 }
 
 // ✅ new endpoint to return report data as JSON
 @GetMapping("/analytics/report")
 @PreAuthorize("hasAuthority('MERCHANT')")
 public ResponseEntity<List<TicketReportResponseDto>> getReport(Authentication authentication) {
     String merchantContact = authentication.getName();
     List<TicketReportResponseDto> reportList = analyticsService.getMerchantReport(merchantContact);
     return ResponseEntity.ok(reportList);
 }

 
 
 
}