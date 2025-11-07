package com.example.demo.Service;



import com.example.demo.Repository.PincodeRepository;
import com.example.demo.entity.Pincode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PincodeService {

    private final PincodeRepository pincodeRepository;

    public Pincode getPincodeDetails(String pincode) {
        return pincodeRepository.findById(pincode)
                .orElseThrow(() -> new RuntimeException("Pincode not found: " + pincode));
    }
}