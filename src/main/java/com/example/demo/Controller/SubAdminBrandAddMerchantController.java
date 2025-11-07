package com.example.demo.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Service.MerchantAdminService;
import com.example.demo.dto.BulkUploadResponseDto;
import com.example.demo.dto.CreateMerchantBySubAdminBrandRequest;
import com.example.demo.dto.SubAdminSummaryDto;
import com.example.demo.dto.UpdateMerchantStatusRequest;
import com.example.demo.entity.Merchant;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/Sub-admin wise add/merchants") // New path for brand admin actions
@PreAuthorize("hasAuthority('BRAND_ADMIN')") // Secure for BRAND_ADMIN
@RequiredArgsConstructor
public class SubAdminBrandAddMerchantController
{
	private final MerchantAdminService merchantService;
//single create merchant
    @PostMapping
    public ResponseEntity<Merchant> createMerchantUnderBrand(
            @RequestBody CreateMerchantBySubAdminBrandRequest request,
            Principal principal) {
                
        // principal.getName() will be the brand admin's email
        Merchant createdMerchant = merchantService.createMerchantByBrandAdmin(request, principal.getName());
        return ResponseEntity.ok(createdMerchant);
    }
    
 // ✅ ADD THIS NEW ENDPOINT FOR BULK UPLOAD bulk create merchant 
    @PostMapping("/upload")
    public ResponseEntity<BulkUploadResponseDto> uploadMerchantsForBrand(
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        // --- Validation Checks ---
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new BulkUploadResponseDto(false, List.of("Please upload an Excel file.")));
        }
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !contentType.equals("application/vnd.ms-excel"))) {
             return ResponseEntity.badRequest().body(new BulkUploadResponseDto(false, List.of("Invalid file type. Please upload an Excel file (.xlsx or .xls).")));
        }

        // --- Process the file ---
        try {
            // Pass the brand admin's email (from the token) to the service
            List<String> results = merchantService.createMerchantsFromExcelForBrandAdmin(file, principal.getName());
            return ResponseEntity.ok(new BulkUploadResponseDto(true, results));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BulkUploadResponseDto(false, List.of("Error processing file: " + e.getMessage())));
        }
    }
    
    @GetMapping("/onlybrandwise")
    public ResponseEntity<List<SubAdminSummaryDto>> getMerchantsForBrand(Principal principal) {
        // principal.getName() will be the brand admin's email
        List<SubAdminSummaryDto> merchants = merchantService.getMerchantsByBrandAdmin(principal.getName());
        return ResponseEntity.ok(merchants);
    }
    
 // ✅ ADD THIS NEW ENDPOINT to update merchant status
    @PutMapping("/{contactNumber}/status")
    public ResponseEntity<Merchant> updateMerchantStatus(
            @PathVariable String contactNumber,
            @RequestBody UpdateMerchantStatusRequest request,
            Principal principal) { // Include Principal for potential future checks

        // **Optional Security Check:** You could add logic here to ensure
        // the merchant being updated actually belongs to the logged-in brand admin.
        // This would involve finding the brand admin by principal.getName(),
        // finding the merchant by contactNumber, and comparing their brand IDs.

        Merchant updatedMerchant = merchantService.updateMerchantStatus(contactNumber, request.isActive());
        return ResponseEntity.ok(updatedMerchant);
    }
    
 // ✅ ADD THIS NEW DELETE ENDPOINT sub-admin will be delete merchant 
    @DeleteMapping("/{merchantId}")
    public ResponseEntity<String> deleteMerchant(
            @PathVariable Long merchantId,
            Principal principal) {

        try {
            // principal.getName() is the brand admin's email
            merchantService.deleteMerchantByBrandAdmin(merchantId, principal.getName());
            return ResponseEntity.ok("Merchant deleted successfully.");
        } catch (RuntimeException e) { // Catch not found errors
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) { // Catch unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting merchant.");
        }
    }
    

}
