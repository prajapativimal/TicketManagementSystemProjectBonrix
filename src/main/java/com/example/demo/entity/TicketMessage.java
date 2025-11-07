package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 2000) // ✅ Removed nullable = false
    private String content; // Renamed from "message" to avoid confusion

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderRole senderRole; // Renamed from "role"

    @Column(nullable = false)
    private String ticketId;

    @Column(nullable = false)
    private String senderName; // Renamed from "name"
    
    // It's still good practice to store the ID of the sender
    private Long senderId;
    
    private String attachments; // To store comma-separated filenames
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isSeen = false; // To track if message has been seen

    
    
}