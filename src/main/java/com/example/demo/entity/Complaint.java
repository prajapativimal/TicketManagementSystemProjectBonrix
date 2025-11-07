package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticketId;

    private Long merchantId;
    private String merchantName;
    private String contactNumber;

    private String deviceOrderId;
    
    

    @Enumerated(EnumType.STRING)
    private ComplaintCategory category;

    @Column(length = 1000)
    private String description;

    private String attachments; // filenames comma-separated

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime slaEndTime; // SLA countdown
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_agent_id") // This creates the foreign key column
    private SupportAgent supportAgent;
    
    @ManyToOne
    @JoinColumn(name = "assigned_agent_id")
    private SupportAgent assignedAgent;
    
 // ✅ ADD THESE NEW FIELDS
    private String serialNumber;
    private String transactionId;
    private String orderId;
    private String storeId;
    
    @Column(length = 500)
    private String address;
    
    // ✅ ADD THESE NEW FIELDS
    private String city;
    private String state;
    private String pincode;
    
    private String brandName;

    // ✅ ADD THESE NEW FIELDS to store the admin-defined names
    private String categoryName; // e.g., "Hardware"
    private String issueName;    // e.g., "Device not working"
    
    // ✅ ADD THIS NEW FIELD
    private String modelNumber;

}

