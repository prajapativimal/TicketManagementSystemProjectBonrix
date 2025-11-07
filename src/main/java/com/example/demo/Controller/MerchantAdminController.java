package com.example.demo.Controller;


import com.example.demo.Service.MerchantAdminService;
import com.example.demo.dto.CreateMerchantRequest;
import com.example.demo.dto.UpdateMerchantStatusRequest;
import com.example.demo.entity.Merchant;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/admin-wise-merchant")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class MerchantAdminController {

    private final MerchantAdminService merchantService;
//add merchant adminn 
    @PostMapping
    public ResponseEntity<Merchant> createMerchant(@RequestBody CreateMerchantRequest request) {
        Merchant createdMerchant = merchantService.createMerchant(request);
        return ResponseEntity.ok(createdMerchant);
    }
    //bulk excel sheet add merchant adminn 

    
    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadMerchants(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(List.of("Please upload an Excel file."));
        }
        // Check if the file is an Excel file (basic check)
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !contentType.equals("application/vnd.ms-excel"))) {
             return ResponseEntity.badRequest().body(List.of("Invalid file type. Please upload an Excel file (.xlsx or .xls)."));
        }

        List<String> results = merchantService.createMerchantsFromExcel(file);
        return ResponseEntity.ok(results);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMerchant(@PathVariable Long id) {
        merchantService.deleteMerchant(id);
        return ResponseEntity.ok("Merchant deleted successfully.");
    }
    
    //active or deactive account merchant logic admin site 
    // ✅ ADD THIS ENDPOINT
    @PutMapping("/{contactNumber}/status")
    public ResponseEntity<Merchant> updateStatus(
            @PathVariable String contactNumber,
            @RequestBody UpdateMerchantStatusRequest request) {
        
        Merchant updatedMerchant = merchantService.updateMerchantStatus(contactNumber, request.isActive());
        return ResponseEntity.ok(updatedMerchant);
    }
    
    
    
}
