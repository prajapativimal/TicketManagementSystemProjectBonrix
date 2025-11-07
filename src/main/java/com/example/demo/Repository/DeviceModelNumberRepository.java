package com.example.demo.Repository;


import com.example.demo.entity.DeviceModelNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceModelNumberRepository extends JpaRepository<DeviceModelNumber, Long>
{

    // ✅ This method efficiently checks if a record exists without fetching the whole object
    boolean existsByModelNumber(String modelNumber);

}