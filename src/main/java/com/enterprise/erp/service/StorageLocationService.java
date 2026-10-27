package com.enterprise.erp.service;

import com.enterprise.erp.exception.DuplicateStorageLocationException;
import com.enterprise.erp.exception.StorageLocationNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import com.enterprise.erp.dto.StorageLocationRequest;
import com.enterprise.erp.dto.StorageLocationResponse;
import com.enterprise.erp.entity.Plant;
import com.enterprise.erp.entity.StorageLocation;
import com.enterprise.erp.exception.PlantNotFoundException;
import com.enterprise.erp.mapper.StorageLocationMapper;
import com.enterprise.erp.repository.PlantRepository;
import com.enterprise.erp.repository.StorageLocationRepository;
//import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StorageLocationService {

    private final StorageLocationRepository storageLocationRepository;
    private final StorageLocationMapper storageLocationMapper;
    private final PlantRepository plantRepository;

    @Transactional
    public StorageLocationResponse createStorageLocation(StorageLocationRequest request){

        //Check existing plant
        Plant plant = plantRepository.findByPlantCode(request.getPlantCode())
                .orElseThrow(()-> new PlantNotFoundException(
                        "Plant not found: "+ request.getPlantCode()
                ));

        //Check for duplicate location code within this plant
        if(storageLocationRepository.
                existsByPlant_IdAndStorageLocationCode(
                        plant.getId(),
                        request.getStorageLocationCode())){
            throw new DuplicateStorageLocationException(
                    "Storage location already exists with code: "
                    +request.getStorageLocationCode()
                    +" in plant: "+ request.getPlantCode()
            );
        }

        //Map request fields into entity
        StorageLocation storageLocation = storageLocationMapper.toEntity(request);

        //set the relationship to the existing plant
        storageLocation.setPlant(plant);

        //Save and return the response
               StorageLocation saved =
                       storageLocationRepository.save(storageLocation);

               return storageLocationMapper.toResponse(saved);

    }

    @Transactional(readOnly = true)
    public List<StorageLocationResponse> getStorageLocationsByPlant(
            String plantCode) {

        // Ensure the plant exists, even if it has no storage locations
        if (!plantRepository.existsByPlantCode(plantCode)) {
            throw new PlantNotFoundException(
                    "Plant not found: " + plantCode
            );
        }

        return storageLocationRepository.findByPlant_PlantCode(plantCode)
                .stream()
                .map(storageLocationMapper::toResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public StorageLocationResponse getStorageLocationByPlantAndCode(
            String plantCode,
            String storageLocationCode) {

        StorageLocation storageLocation = storageLocationRepository
                .findByPlant_PlantCodeAndStorageLocationCode(
                        plantCode, storageLocationCode)
                .orElseThrow(() -> new StorageLocationNotFoundException(
                        "Storage location " + storageLocationCode
                                + " not found in plant " + plantCode));

        return storageLocationMapper.toResponse(storageLocation);
    }

    public List<StorageLocationResponse> getAllStorageLocations() {
        return storageLocationRepository.findAll()
                .stream()
                .map(storageLocationMapper::toResponse)
                .toList();
    }

}
