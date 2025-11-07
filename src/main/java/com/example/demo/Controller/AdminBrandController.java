package com.example.demo.Controller;



import com.example.demo.Service.AdminBrandService;
import com.example.demo.dto.AdminCreateBrandRequest;
import com.example.demo.dto.UpdateBrandRequest;
import com.example.demo.entity.AdminBrand;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/admin/brands")
@PreAuthorize("hasRole('ADMIN')") // Secures all endpoints in this controller
@RequiredArgsConstructor
public class AdminBrandController {

    private final AdminBrandService service;

 // ✅ UPDATE THIS METHOD
    @PostMapping
    public ResponseEntity<AdminBrand> createBrand(@RequestBody AdminCreateBrandRequest request) {
        return ResponseEntity.ok(service.createBrand(request));
    }

    @GetMapping
    public ResponseEntity<List<AdminBrand>> getAllBrands() {
        return ResponseEntity.ok(service.getAllBrands());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminBrand> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getBrandById(id));
    }

 // ✅ UPDATE THIS METHOD
    @PutMapping("/{id}")
    public ResponseEntity<AdminBrand> updateBrand(@PathVariable Long id, @RequestBody UpdateBrandRequest request) {
        return ResponseEntity.ok(service.updateBrand(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable Long id) {
        service.deleteBrand(id);
        return ResponseEntity.ok("Brand deleted successfully.");
    }
}