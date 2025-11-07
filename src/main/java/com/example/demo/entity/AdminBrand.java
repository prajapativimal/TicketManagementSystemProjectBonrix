package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "admin_brand")
@Data
public class AdminBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String brandName;
    
    // ✅ ADD THESE FIELDS
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
    
 // ✅ ADD THESE NEW FIELDS
    private String brandCategory;
    private String registeredAddress;
    private String pincode;
    private String city;
    private String state;
    
    @Column(nullable = false, unique = true)
    private String contactNumber;
    
    
    
    
}