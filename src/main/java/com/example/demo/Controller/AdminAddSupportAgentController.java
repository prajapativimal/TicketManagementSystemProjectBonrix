package com.example.demo.Controller;

import com.example.demo.Service.SupportAgentAdminService;
import com.example.demo.dto.AdminAddSupportAgentRequest;
import com.example.demo.entity.SupportAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/support-agents")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAddSupportAgentController {

    private final SupportAgentAdminService agentService;

    @PostMapping("/add")
    public ResponseEntity<?> createAgent(@RequestBody AdminAddSupportAgentRequest request) {
        try {
            SupportAgent createdAgent = agentService.createAgent(request);
            return ResponseEntity.ok(createdAgent);
        } catch (RuntimeException ex) {
            // Return only the message in JSON format
            return ResponseEntity.ok(Map.of("message", ex.getMessage()));

        }
    }


   /* @GetMapping
    public ResponseEntity<List<SupportAgent>> getAllAgents() {
        return ResponseEntity.ok(agentService.getAllAgents());
    }*/

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAgent(@PathVariable Long id) {
        agentService.deleteAgent(id);
        return ResponseEntity.ok("Support agent deleted successfully.");
    }
}