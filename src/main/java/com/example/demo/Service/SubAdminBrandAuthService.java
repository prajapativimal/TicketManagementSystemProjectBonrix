package com.example.demo.Service;

import com.example.demo.Repository.AdminBrandRepository;
import com.example.demo.config.JwtUtil;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.SubAdminBrandLoginRequest;
import com.example.demo.entity.AdminBrand;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubAdminBrandAuthService
{
	private final AdminBrandRepository brandRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(SubAdminBrandLoginRequest request) {
        AdminBrand brand = brandRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), brand.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(brand.getEmail(), brand.getRole().name());
        return new AuthResponse(token, brand.getBrandName());
    }

}
