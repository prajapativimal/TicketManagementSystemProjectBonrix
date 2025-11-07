package com.example.demo.dto;



import com.example.demo.entity.ComplaintCategory;
import com.example.demo.entity.Priority;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ComplaintRequest {
    private String deviceOrderId;
    private ComplaintCategory category;
    private String description;
   // private Priority priority;
    private MultipartFile[] attachments;
    
 // ✅ ADD THESE NEW FIELDS
    private String serialNumber;
    private String transactionId;
    
    
    
//    private String orderId;
    private String storeId;
    private String address;
    private String contactNumber; // The contact number specific to this complaint
    
    // ✅ ADD THESE NEW FIELDS
    private String city;
    private String state;
    private String pincode;
    
    
    // ✅ ADD THESE NEW FIELDS
    private String categoryName; // Corresponds to 'categoryname' in your cURL
    private String issues;       // Corresponds to 'issues' in your cURL
  

    // ✅ ADD THIS NEW FIELD
    private String modelNumber;
    private String brandName;

    
}
