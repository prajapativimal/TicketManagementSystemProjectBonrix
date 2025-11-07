package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "device_model_number") // Sets the custom table name
@Data
public class DeviceModelNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "model_number", unique = true, nullable = false)
    private String modelNumber;
}