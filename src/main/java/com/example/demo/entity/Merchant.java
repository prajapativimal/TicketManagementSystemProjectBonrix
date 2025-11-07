package com.example.demo.entity;




import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String merchantName;

    @Column(unique = true, nullable = false)
    private String contactNumber;

    private String businessName;
    

    @Enumerated(EnumType.STRING)
    private Role role;   // USER or ADMIN

   private String otp;  // store latest OTP for login
   
   
   // ✅ ADD THIS RELATIONSHIP
   @ManyToOne
   @JoinColumn(name = "brand_id")
   private AdminBrand brand;
   
   // ✅ ADD THIS FIELD
   private boolean active = true;
}
