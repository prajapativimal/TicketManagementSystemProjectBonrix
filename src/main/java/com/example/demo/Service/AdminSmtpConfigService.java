package com.example.demo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.Repository.AdminSmtpConfigRepository;
import com.example.demo.entity.AdminSmtpConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminSmtpConfigService {

    private final AdminSmtpConfigRepository adminsmtpconfigrepository;

    // Save or Update (assuming only one config exists)
    public AdminSmtpConfig saveOrUpdateConfig(AdminSmtpConfig config) {
        Optional<AdminSmtpConfig> existingOpt = adminsmtpconfigrepository.findFirstByOrderByIdAsc();
        if (existingOpt.isPresent()) {
        	AdminSmtpConfig existing = existingOpt.get();
            // Update existing record
            existing.setEmail(config.getEmail());
            existing.setPassword(config.getPassword()); // Consider encryption
            existing.setHost(config.getHost());
            existing.setPort(config.getPort());
            existing.setTime(config.getTime()); // Update time
            return adminsmtpconfigrepository.save(existing);
        } else {
             // Create a new record if none exists
             config.setId(null); // Ensure it's treated as new
             return adminsmtpconfigrepository.save(config);
        }
    }

    // Get the current config
    public Optional<AdminSmtpConfig> getCurrentConfig() {
        return adminsmtpconfigrepository.findFirstByOrderByIdAsc();
    }

    // Read All (if needed, though usually just one record)
    public List<AdminSmtpConfig> getAllConfigs() {
        return adminsmtpconfigrepository.findAll();
    }

    // Delete (if needed, though usually just updated)
    public void deleteConfig(Long id) {
    	adminsmtpconfigrepository.deleteById(id);
    }
    
    //
 // ✅ ADD THIS METHOD to update the active status
    public AdminSmtpConfig updateSmtpStatusByEmail(String email, boolean isActive) {
    	AdminSmtpConfig config = adminsmtpconfigrepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("SMTP Configuration not found for email: " + email));

        config.setActive(isActive);
        return adminsmtpconfigrepository.save(config);
    }
 }














///AdminSmtpConfigService