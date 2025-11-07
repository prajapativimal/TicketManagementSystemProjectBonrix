package com.example.demo.Repository;



import com.example.demo.entity.Pincode;
import org.springframework.data.jpa.repository.JpaRepository;

// Use String as the ID type because the pincode is a String
public interface PincodeRepository extends JpaRepository<Pincode, String> {
}