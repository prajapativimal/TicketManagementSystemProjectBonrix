package com.example.demo.Service;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.ComplaintRepository;
import com.example.demo.Repository.SupportAgentRepository;
import com.example.demo.dto.*;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.ComplaintStatus;
import com.example.demo.entity.SupportAgent;
import com.example.demo.config.JwtUtil;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    private final ComplaintRepository complaintRepository;
    private final SupportAgentRepository supportAgentRepository;
    private final JwtUtil jwtUtil;

    public String register(AdminRegisterRequest request) {
        if (adminRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Admin admin = Admin.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_ADMIN")
                .build();

        adminRepository.save(admin);
        return "Admin registered successfully!";
    }

    public AuthResponse login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(admin.getEmail(), admin.getRole());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRole(admin.getRole());
        return response;
    }
    //
    public String assignComplaintsToAgentByIds(AssignByNameRequest request) {
        // 1. Find the support agent by name
        SupportAgent agent = supportAgentRepository.findByName(request.agentName())
                .orElseThrow(() -> new RuntimeException("Support Agent not found with name: " + request.agentName()));

        // 2. Fetch complaints by IDs
        List<Complaint> complaintsToAssign = complaintRepository.findAllById(request.complaintIds());

        if (complaintsToAssign.isEmpty()) {
            return "No complaints found for the given IDs.";
        }

        // 3. Assign each complaint to the agent and update status
        for (Complaint complaint : complaintsToAssign) {
            complaint.setSupportAgent(agent);
            complaint.setStatus(ComplaintStatus.ASSIGNED);
        }

        // 4. Save all updates
        complaintRepository.saveAll(complaintsToAssign);

        return String.format("%d complaints have been successfully assigned to %s.",
                complaintsToAssign.size(), agent.getName());
    }
    
    //support account active and deactive for admin 
    // ✅ ADD THIS METHOD
    public SupportAgent updateAgentStatusByEmail(String email, boolean isActive) {
        // 1. Find the agent by their email
        SupportAgent agent = supportAgentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Support Agent not found with email: " + email));
        
        // 2. Update their active status
        agent.setActive(isActive);
        
        // 3. Save and return the updated agent
        return supportAgentRepository.save(agent);
    }

    
}
