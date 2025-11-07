package com.example.demo.Controller;




import com.example.demo.Service.AdminAuthService;
import com.example.demo.dto.*;
import com.example.demo.entity.Merchant;
import com.example.demo.entity.SupportAgent;
import com.example.demo.Repository.MerchantRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final MerchantRepository merchantRepository;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AdminRegisterRequest request) {
        return ResponseEntity.ok(adminAuthService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AdminLoginRequest request) {
        return ResponseEntity.ok(adminAuthService.login(request));
    }

    // Get all merchants (Admin only)
    @GetMapping("/merchants")
    public ResponseEntity<List<Merchant>> getAllMerchants() {
        List<Merchant> merchants = merchantRepository.findAll();
        return ResponseEntity.ok(merchants);
    }
    
    
    // 

    @PostMapping("/complaints/assign-by-name")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> assignComplaintsByName(@RequestBody AssignByNameRequest request) {
        String result = adminAuthService.assignComplaintsToAgentByIds(request);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{email}/status")
    public ResponseEntity<SupportAgent> updateStatus(
            @PathVariable String email,
            @RequestBody UpdateSupportAgentStatusRequest request) {
        
        SupportAgent updatedAgent = adminAuthService.updateAgentStatusByEmail(email, request.isActive());
        return ResponseEntity.ok(updatedAgent);
    }

}

