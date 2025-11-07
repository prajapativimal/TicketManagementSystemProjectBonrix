package com.example.demo.Controller;







import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.AdminBrand;
import com.example.demo.entity.AdminCategory;





import com.example.demo.Repository.MerchantRepository;
import com.example.demo.Service.AdminBrandService;
import com.example.demo.Service.CategoryAdminService;

import com.example.demo.Service.MerchantService;

import com.example.demo.config.JwtUtil;

import com.example.demo.entity.Merchant;



import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;







import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;



import java.security.Principal;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

import java.util.Optional;



@RestController

@SecurityRequirement(name = "Bearer Authentication")

@CrossOrigin(origins = "*", allowedHeaders = "*")

@RequestMapping("/api/merchant")

@RequiredArgsConstructor

public class MerchantController {



    private final MerchantService merchantService;
    private final AdminBrandService adminBrandService;


    private final JwtUtil jwtUtil;

    private final CategoryAdminService categoryService;



private final MerchantRepository merchantRepo;

    // Registration API

  

  @PostMapping("/register")
   public ResponseEntity<Merchant> register(@RequestBody Map<String, String> payload) {
    Merchant saved = merchantService.registerMerchant(payload);
    return ResponseEntity.ok(saved);
}



    // Request OTP API

    @PostMapping("/request-otp")

    public ResponseEntity<String> requestOtp(@RequestParam String contactNumber) {

        String otp = merchantService.generateOtp(contactNumber);

        return ResponseEntity.ok("OTP sent successfully: " + otp);

    }



    // Login API -> Generate JWT

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String contactNumber, 
                                                     @RequestParam String otp) {
        Map<String, Object> response = new HashMap<>();

        // ✅ Add a try-catch block to handle the specific error
        try {
            return merchantService.verifyOtp(contactNumber, otp)
                    .map(merchant -> {
                        // This is the success path
                        String token = jwtUtil.generateToken(
                                merchant.getContactNumber(),
                                merchant.getRole().name()
                        );
                        response.put("message", "Login successful");
                        response.put("status", true);
                        response.put("token", token);
                        response.put("role", merchant.getRole());
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        // This handles invalid OTP or contact number
                        response.put("message", "Invalid OTP or contact number");
                        response.put("status", false);
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                    });
        } catch (RuntimeException e) {
            // ✅ This catches the "deactivated account" error from your service
            response.put("message", e.getMessage()); // e.g., "This merchant account has been deactivated."
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }
    

 // ✅ Accessible only by USER role

    @GetMapping("/profile")

    @PreAuthorize("hasAuthority('ROLE_USER')")

    public ResponseEntity<?> getUserProfile(Principal principal) {

        String contactNumber = principal.getName(); // JWT subject = contactNumber



        Optional<Merchant> merchantOpt = merchantRepo.findByContactNumber(contactNumber);



        return merchantOpt

                .map(ResponseEntity::ok)

                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping("/categories")

    @PreAuthorize("hasAuthority('MERCHANT')")

    public ResponseEntity<List<AdminCategory>> getAllCategories() {

        return ResponseEntity.ok(categoryService.getAllCategories());

    }


    @GetMapping("/brands")
   // @PreAuthorize("hasAuthority('MERCHANT')") // Secures the endpoint for merchants only
    public ResponseEntity<List<AdminBrand>> getAllBrands() {
        return ResponseEntity.ok(adminBrandService.getAllBrands());
    }



}