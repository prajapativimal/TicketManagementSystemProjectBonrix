

package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Admin_SmtpConfig") // Sets the table name
@Data
public class AdminSmtpConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Usually only one row needed

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // WARNING: Store securely!

    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private int port;

    @Column(nullable = false)
    private int time; // Time interval in minutes
    
 // ✅ ADD THIS FIELD
    @Column(nullable = false)
    private boolean active = true; // Default to active
}

