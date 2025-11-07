package com.example.demo.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pincodes")
@Data
public class Pincode {

    @Id
    private String pincode; // The pincode is the primary key

    private String state;
    private String city;
}