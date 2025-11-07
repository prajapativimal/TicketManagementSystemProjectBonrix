// In a new file: dto/FeedbackRequest.java
package com.example.demo.dto;

public record FeedbackRequest(
    int rating,
    String comment,
    String ticketId
) {}

