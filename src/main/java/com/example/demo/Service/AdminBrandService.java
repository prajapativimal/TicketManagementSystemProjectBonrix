package com.example.demo.Service;


import com.example.demo.Repository.AdminBrandRepository;
import com.example.demo.Repository.PincodeRepository;
import com.example.demo.dto.AdminCreateBrandRequest;
import com.example.demo.dto.UpdateBrandRequest;
import com.example.demo.entity.AdminBrand;
import com.example.demo.entity.Pincode;
import com.example.demo.entity.Role;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBrandService {

    private final AdminBrandRepository repository;
    private final PasswordEncoder passwordEncoder; // ✅ Inject the PasswordEncoder

    private final PincodeRepository pincodeRepository; // ✅ Inject
 // ✅ UPDATE THIS METHOD
    public AdminBrand createBrand(AdminCreateBrandRequest request) {
        repository.findByEmail(request.getEmail()).ifPresent(b -> {
            throw new RuntimeException("Email already exists");
        });

        AdminBrand brand = new AdminBrand();
        brand.setBrandName(request.getBrandName());
        brand.setEmail(request.getEmail());
        brand.setPassword(passwordEncoder.encode(request.getPassword()));
        brand.setRole(Role.BRAND_ADMIN);
        brand.setBrandCategory(request.getBrandCategory());
        brand.setRegisteredAddress(request.getRegisteredAddress());
        brand.setPincode(request.getPincode());
        
     // ✅ Set the new contact number
        brand.setContactNumber(request.getContactNumber());

        // ✅ Add Pincode lookup logic
        if (request.getPincode() != null && !request.getPincode().isEmpty()) {
            Pincode pincodeData = pincodeRepository.findById(request.getPincode())
                    .orElseThrow(() -> new RuntimeException("Invalid Pincode provided."));
            brand.setCity(pincodeData.getCity());
            brand.setState(pincodeData.getState());
        }

        return repository.save(brand);
    }

    // Read All
    public List<AdminBrand> getAllBrands() {
        return repository.findAll();
    }

    // Read One
    public AdminBrand getBrandById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));
    }

 // ✅ UPDATE THIS METHOD
    public AdminBrand updateBrand(Long id, UpdateBrandRequest request) {
        AdminBrand existingBrand = getBrandById(id);

        // Update fields only if they are provided in the request
        if (request.getBrandName() != null) {
            existingBrand.setBrandName(request.getBrandName());
        }
        if (request.getEmail() != null) {
            existingBrand.setEmail(request.getEmail());
        }
        if (request.getBrandCategory() != null) {
            existingBrand.setBrandCategory(request.getBrandCategory());
        }
        if (request.getRegisteredAddress() != null) {
            existingBrand.setRegisteredAddress(request.getRegisteredAddress());
        }
        
        // Hash the password only if a new one is provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingBrand.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Look up city and state if a new pincode is provided
        if (request.getPincode() != null && !request.getPincode().isEmpty()) {
            Pincode pincodeData = pincodeRepository.findById(request.getPincode())
                    .orElseThrow(() -> new RuntimeException("Invalid Pincode provided."));
            existingBrand.setPincode(request.getPincode());
            existingBrand.setCity(pincodeData.getCity());
            existingBrand.setState(pincodeData.getState());
        }

        return repository.save(existingBrand);
    }

    // Delete
    public void deleteBrand(Long id) {
        repository.deleteById(id);
    }
}