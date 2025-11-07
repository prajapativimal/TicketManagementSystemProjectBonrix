package com.example.demo.Service;

import com.example.demo.Repository.SupportAgentRepository;
import com.example.demo.dto.*;
import com.example.demo.entity.Role;
import com.example.demo.entity.SupportAgent;
import com.example.demo.config.JwtUtil;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportAgentAuthService {

    private final SupportAgentRepository supportAgentRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // ✅ inject JwtUtil

    // ✅ Register Support Agent
    public String register(SupportAgentRegisterRequest request) {
        if (supportAgentRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        SupportAgent agent = SupportAgent.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.SUPPORT_AGENT)
                .build();

        supportAgentRepo.save(agent);
        return "Support Agent registered successfully";
    }

    // ✅ Login Support Agent
    public AuthResponse login(SupportAgentLoginRequest request) {
        SupportAgent agent = supportAgentRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        // ✅ ADD THIS CHECK
        if (!agent.isActive()) {
            throw new RuntimeException("This support agent account has been deactivated.");
        }

        if (!passwordEncoder.matches(request.getPassword(), agent.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(agent.getEmail(), agent.getRole().name());
        return new AuthResponse(token, agent.getRole().name());
    }
    //
    
    // ✅ Fetch all support agents
    public List<SupportAgent> getAllSupportAgents() {
        return supportAgentRepo.findAll();
    }
}
