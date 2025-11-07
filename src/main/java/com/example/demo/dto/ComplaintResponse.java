package com.example.demo.dto;




import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ComplaintResponse {

    private Long id;
    private String ticketId;
    private String merchantName;
    private String category;
    private String description;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime slaEndTime;
    private String deviceOrderId;   // ✅ add this
    // ✅ ADD THIS FIELD
    private List<String> attachmentUrls;


    private String supportAgentName;  // For the 'supportAgent' field
    private String assignedAgentName; // For the 'assignedAgent' field
    //
    // ✅ ADD THESE NEW FIELDS
    private String serialNumber;
    private String transactionId;
//    private String orderId;
    private String storeId;
    private String address;
    private String contactNumber;
    
 // ✅ ADD THESE THREE FIELDS
    private String city;
    private String state;
    private String pincode;
    
    private String categoryName; // Corresponds to 'categoryname' in your cURL
    private String issues;       // Corresponds to 'issues' in your cURL
    // ✅ ADD THIS NEW FIELD
    private String modelNumber;
    private String brandName;

    // getters and setters
}
