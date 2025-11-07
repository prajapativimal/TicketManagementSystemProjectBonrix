package com.example.demo.entity;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "support_agents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class SupportAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.SUPPORT_AGENT; // default role
    
    private boolean active = true;
    
    // ✅ ADD THIS RELATIONSHIP
    @OneToOne
    @JoinColumn(name = "brand_id", unique = true) // 'unique = true' enforces your rule
    private AdminBrand brand;
    
    

}
