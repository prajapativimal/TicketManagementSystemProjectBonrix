package com.example.demo.entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;               // Category name
    private String description;        // Category description
    private int responseSlaHours;      // Response SLA in hours
    private int resolutionSlaHours;    // Resolution SLA in hours
}
