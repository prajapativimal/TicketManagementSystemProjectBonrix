package com.example.demo.Service;


import com.example.demo.Repository.DeviceModelNumberRepository;
import com.example.demo.entity.DeviceModelNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceModelNumberService {

    private final DeviceModelNumberRepository repository;

    // Create
    public DeviceModelNumber createModel(DeviceModelNumber model) {
        return repository.save(model);
    }

    // Read All
    public List<DeviceModelNumber> getAllModels() {
        return repository.findAll();
    }

    // Read One
    public DeviceModelNumber getModelById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("DeviceModelNumber not found with id: " + id));
    }

    // Update
    public DeviceModelNumber updateModel(Long id, DeviceModelNumber modelDetails) {
        DeviceModelNumber existingModel = getModelById(id);
        existingModel.setModelNumber(modelDetails.getModelNumber());
        return repository.save(existingModel);
    }

    // Delete
    public void deleteModel(Long id) {
        repository.deleteById(id);
    }
}
