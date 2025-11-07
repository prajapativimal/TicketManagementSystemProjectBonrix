package com.example.demo.Repository;



import com.example.demo.entity.AdminBrand;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminBrandRepository extends JpaRepository<AdminBrand, Long> 
{
    Optional<AdminBrand> findByBrandName(String brandName);
    

    // ✅ ADD THIS METHOD for validation and login
    Optional<AdminBrand> findByEmail(String email);
    
  


}