package com.example.demo.Controller;



import com.example.demo.Service.PincodeService;
import com.example.demo.entity.Pincode;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/merchant/pincodes")
@RequiredArgsConstructor
public class PincodeController {

    private final PincodeService pincodeService;

    @GetMapping("/{pincode}")
    @PreAuthorize("hasAuthority('MERCHANT','ADMIN')")
    public ResponseEntity<Pincode> getPincodeInfo(@PathVariable String pincode) {
        Pincode pincodeDetails = pincodeService.getPincodeDetails(pincode);
        return ResponseEntity.ok(pincodeDetails);
    }
}