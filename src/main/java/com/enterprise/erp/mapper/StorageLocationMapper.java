package com.enterprise.erp.mapper;

import com.enterprise.erp.dto.StorageLocationRequest;
import com.enterprise.erp.dto.StorageLocationResponse;
import com.enterprise.erp.entity.StorageLocation;
import org.springframework.stereotype.Component;

@Component
public class StorageLocationMapper {
    public StorageLocationResponse toResponse(
            StorageLocation storageLocation
    ) {
        StorageLocationResponse response = new StorageLocationResponse();

        response.setId(storageLocation.getId());
        response.setStorageLocationCode(storageLocation.getStorageLocationCode());
        response.setStorageLocationName((storageLocation.getStorageLocationName()));
        response.setPlantCode(storageLocation.getPlant().getPlantCode());
        response.setPlantName(storageLocation.getPlant().getPlantName());
        response.setCreatedAt(storageLocation.getCreatedAt());

        return response;

    }

    public StorageLocation toEntity(StorageLocationRequest request) {

        StorageLocation storageLocation = new StorageLocation();

        storageLocation.setStorageLocationCode(request.getStorageLocationCode());
        storageLocation.setStorageLocationName(request.getStorageLocationName());
        return storageLocation;
    }
}