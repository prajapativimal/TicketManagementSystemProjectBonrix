// In a new file: entity/Feedback.java
package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The star rating, e.g., 1 to 5
    @Column(nullable = false)
    private int rating;

    // An optional text comment
    @Column(length = 1000)
    private String comment;

    private LocalDateTime createdAt;

    // Link to the ticket ID for which feedback is given
    @Column(name = "ticket_id")
    private String ticketId;

    // Link to the merchant who gave the feedback
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
}