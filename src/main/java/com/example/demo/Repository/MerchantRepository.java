package com.example.demo.Repository;





import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.AdminBrand;
import com.example.demo.entity.Merchant;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByContactNumber(String contactNumber);
 // ✅ Add this method to find all merchants belonging to a specific brand object
    List<Merchant> findByBrand(AdminBrand brand);
    
   

}