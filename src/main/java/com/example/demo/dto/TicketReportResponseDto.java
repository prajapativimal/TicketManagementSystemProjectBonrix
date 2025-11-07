package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketReportResponseDto {
    private String ticketId;
    private String categoryName;
    private String brandName;
    private String priority;
    private String status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime slaEndTime;
    private String city;
    private String state;
    private String pincode;
    private String modelNumber;
    private String serialNumber;
    private String orderId;
    private String transactionId;
    private String storeId;
}




