package com.example.demo.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.AdminSmtpConfig;

import java.util.Optional;
public interface AdminSmtpConfigRepository extends JpaRepository<AdminSmtpConfig, Long>
{
	// Helper to find the active configuration
    Optional<AdminSmtpConfig> findFirstByOrderByIdAsc();
    
 // ✅ ADD THIS METHOD
    Optional<AdminSmtpConfig> findByEmail(String email);

	Optional<AdminSmtpConfig> findFirstByActiveTrueOrderByIdAsc();

}
