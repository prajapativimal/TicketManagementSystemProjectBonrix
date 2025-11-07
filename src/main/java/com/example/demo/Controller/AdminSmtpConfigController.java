
package com.example.demo.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Service.AdminSmtpConfigService;
import com.example.demo.entity.AdminSmtpConfig;

import java.util.List; // Import List
import java.util.Map;

@RestController
@RequestMapping("/api/admin-smtp-config")
@PreAuthorize("hasRole('ADMIN')") // Secures all endpoints
@RequiredArgsConstructor
public class AdminSmtpConfigController {

    private final AdminSmtpConfigService service;

    // Use PUT to create or update the single SMTP configuration entry
    @PutMapping
    public ResponseEntity<AdminSmtpConfig> saveOrUpdateConfig(@RequestBody AdminSmtpConfig config) {
    	AdminSmtpConfig savedConfig = service.saveOrUpdateConfig(config);
        // Censor password in the response for security
        savedConfig.setPassword("********");
        return ResponseEntity.ok(savedConfig);
    }

    // GET the current configuration (password censored)
    @GetMapping
    public ResponseEntity<AdminSmtpConfig> getCurrentConfig() {
        return service.getCurrentConfig()
                .map(config -> {
                    config.setPassword("********"); // Censor password
                    return ResponseEntity.ok(config);
                })
                .orElse(ResponseEntity.notFound().build()); // Return 404 if no config set yet
    }

    // Optional: Endpoint to get all configurations (usually only one)
    @GetMapping("/all")
    public ResponseEntity<List<AdminSmtpConfig>> getAllConfigs() {
        List<AdminSmtpConfig> configs = service.getAllConfigs();
        // Censor passwords in the list
        configs.forEach(config -> config.setPassword("********"));
        return ResponseEntity.ok(configs);
    }

    // Optional: Endpoint to delete a configuration
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteConfig(@PathVariable Long id) {
        service.deleteConfig(id);
        return ResponseEntity.ok("SMTP Configuration deleted successfully.");
    }
    
    
 // ✅ ADD THIS NEW ENDPOINT to toggle  admin will be active status by email for mail sending 
    @PutMapping("/{email}/status")
    public ResponseEntity<AdminSmtpConfig> updateStatus(
            @PathVariable String email,
            @RequestBody Map<String, Boolean> requestBody) { // Use a Map to get the 'active' boolean

        Boolean isActive = requestBody.get("active");
        if (isActive == null) {
            return ResponseEntity.badRequest().build(); // Or return a specific error message
        }

        AdminSmtpConfig updatedConfig = service.updateSmtpStatusByEmail(email, isActive);
        updatedConfig.setPassword("********"); // Censor password in response
        return ResponseEntity.ok(updatedConfig);
    }
}









