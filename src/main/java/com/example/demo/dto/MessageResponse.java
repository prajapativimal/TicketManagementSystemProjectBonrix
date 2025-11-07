package com.example.demo.dto;


import java.time.LocalDateTime;

public record MessageResponse(
    Long id,
    String content,
    LocalDateTime createdAt,
    Long merchantId,
    String merchantName,
    String ticketId // ✅ Add this field

) 
{
	
}