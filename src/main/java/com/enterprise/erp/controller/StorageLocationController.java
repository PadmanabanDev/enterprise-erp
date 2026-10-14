
package com.enterprise.erp.controller;

import com.enterprise.erp.dto.StorageLocationRequest;
import com.enterprise.erp.dto.StorageLocationResponse;
import com.enterprise.erp.service.StorageLocationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/storage-locations")
public class StorageLocationController {

    private final StorageLocationService storageLocationService;

    public StorageLocationController(
            StorageLocationService storageLocationService) {
        this.storageLocationService = storageLocationService;
    }

    @PostMapping
    public StorageLocationResponse createStorageLocation(
            @Valid @RequestBody StorageLocationRequest request) {

        return storageLocationService.createStorageLocation(request);
    }

    @GetMapping("/plant/{plantCode}")
    public List<StorageLocationResponse> getStorageLocationsByPlant(
            @PathVariable String plantCode) {

        return storageLocationService.getStorageLocationsByPlant(plantCode);
    }
}
