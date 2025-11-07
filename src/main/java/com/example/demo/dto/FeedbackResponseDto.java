// In a new file: dto/FeedbackResponseDto.java
package com.example.demo.dto;

import java.time.LocalDateTime;

public record FeedbackResponseDto(
    Long id,
    int rating,
    String comment,
    String ticketId,
    LocalDateTime createdAt,
    MerchantSummaryDto merchant // Nested merchant summary
) {}