package com.example.demo.Service;

import com.example.demo.Repository.AdminBrandRepository;
import com.example.demo.Repository.MerchantRepository;
import com.example.demo.entity.AdminBrand;
import com.example.demo.entity.Merchant;
import com.example.demo.entity.Role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepo;
    private final AdminBrandRepository brandRepository;


   
    public Merchant registerMerchant(Map<String, String> payload) {
        // 1. Extract the brand name from the payload
        String brandName = payload.get("brandName");
        if (brandName == null || brandName.isEmpty()) {
            throw new RuntimeException("brandName is required.");
        }

        // 2. Find the brand in the database using its name
        AdminBrand brand = brandRepository.findByBrandName(brandName)
                .orElseThrow(() -> new RuntimeException("Brand '" + brandName + "' not found."));

        // 3. Create a new Merchant object manually
        Merchant newMerchant = new Merchant();
        newMerchant.setMerchantName(payload.get("merchantName"));
        newMerchant.setContactNumber(payload.get("contactNumber"));
        newMerchant.setBusinessName(payload.get("businessName"));
        newMerchant.setRole(Role.MERCHANT); // Set default role

        // 4. Associate the found brand with the new merchant
        newMerchant.setBrand(brand);

        // 5. Save the new merchant to the database
        return merchantRepo.save(newMerchant);
    }

    
    public String generateOtp(String contactNumber) {
        Optional<Merchant> merchantOpt = merchantRepo.findByContactNumber(contactNumber);
        if (merchantOpt.isPresent()) {
            String otp = String.valueOf(new Random().nextInt(9000) + 1000);
            Merchant merchant = merchantOpt.get();
            merchant.setOtp(otp);
            merchantRepo.save(merchant);
            return otp;
        }
        throw new RuntimeException("Merchant not found with contact number: " + contactNumber);
    }


 // In your MerchantService.java (or wherever you verify the OTP)

    public Optional<Merchant> verifyOtp(String contactNumber, String otp) {
        Optional<Merchant> merchantOpt = merchantRepo.findByContactNumber(contactNumber);
        
        if (merchantOpt.isPresent()) {
            Merchant merchant = merchantOpt.get();
            
            // ✅ ADD THIS CHECK
            if (!merchant.isActive()) {
                throw new RuntimeException("This merchant account has been deactivated.");
            }

            if (merchant.getOtp() != null && merchant.getOtp().equals(otp)) {
                merchant.setOtp(null);
                merchantRepo.save(merchant);
                return Optional.of(merchant);
            }
        }
        return Optional.empty();
    }
}
