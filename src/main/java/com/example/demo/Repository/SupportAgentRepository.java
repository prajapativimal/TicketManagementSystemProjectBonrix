package com.example.demo.Repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.AdminBrand;
import com.example.demo.entity.SupportAgent;

public interface SupportAgentRepository extends JpaRepository<SupportAgent, Long> {
    Optional<SupportAgent> findByEmail(String email);

    // ✅ ADD THIS METHOD
    Optional<SupportAgent> findByName(String name);

	List<SupportAgent> findByActiveTrue();
	
	// ✅ ADD THIS METHOD to check if a brand is already in use
    boolean existsByBrand(AdminBrand brand);

	Optional<SupportAgent> findByBrand(AdminBrand brand);
	
	
}