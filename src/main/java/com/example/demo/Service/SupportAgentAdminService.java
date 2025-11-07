package com.example.demo.Service;




import com.example.demo.Repository.AdminBrandRepository;
import com.example.demo.Repository.SupportAgentRepository;
import com.example.demo.dto.AdminAddSupportAgentRequest;
import com.example.demo.entity.AdminBrand;
import com.example.demo.entity.Role;
import com.example.demo.entity.SupportAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportAgentAdminService {

    private final SupportAgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder; // Inject the password encoder
    private final AdminBrandRepository brandRepository; // ✅ Inject AdminBrandRepository

    // Create a new Support Agent
    public SupportAgent createAgent(AdminAddSupportAgentRequest request) {
        if (agentRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already in use.");
        }
        
     // ✅ 1. Find the brand by its name
        AdminBrand brand = brandRepository.findByBrandName(request.getBrandName())
                .orElseThrow(() -> new RuntimeException("Brand '" + request.getBrandName() + "' not found."));

        // ✅ 2. Check if this brand is already assigned to another agent
        if (agentRepository.existsByBrand(brand)) {
            throw new RuntimeException("Brand '" + request.getBrandName() + "' is already assigned to another support agent.");
        }

        SupportAgent agent = new SupportAgent();
        agent.setName(request.getName());
        agent.setEmail(request.getEmail());
        // ✅ Hash the password before saving
        agent.setPassword(passwordEncoder.encode(request.getPassword()));
        agent.setRole(Role.SUPPORT_AGENT);
        agent.setActive(true); // Active by default
        agent.setBrand(brand); // ✅ 3. Set the brand object

        return agentRepository.save(agent);
    }

    // Read all Support Agents
    public List<SupportAgent> getAllAgents() {
        return agentRepository.findAll();
    }

    // Delete a Support Agent
    public void deleteAgent(Long id) {
        agentRepository.deleteById(id);
    }
}