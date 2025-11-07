package com.example.demo.Controller;



import com.example.demo.Service.DeviceModelNumberService;
import com.example.demo.entity.DeviceModelNumber;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/device-models")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@PreAuthorize("hasRole('ADMIN')") // Secures all endpoints in this controller
@RequiredArgsConstructor
public class DeviceModelNumberController {

    private final DeviceModelNumberService service;

    @PostMapping
    public ResponseEntity<DeviceModelNumber> createModel(@RequestBody DeviceModelNumber model) {
        return ResponseEntity.ok(service.createModel(model));
    }

    @GetMapping
    public ResponseEntity<List<DeviceModelNumber>> getAllModels() {
        return ResponseEntity.ok(service.getAllModels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceModelNumber> getModelById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getModelById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceModelNumber> updateModel(@PathVariable Long id, @RequestBody DeviceModelNumber model) {
        return ResponseEntity.ok(service.updateModel(id, model));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteModel(@PathVariable Long id) {
        service.deleteModel(id);
        return ResponseEntity.ok("Device model deleted successfully.");
    }
}