package com.enterprise.erp.repository;

import com.enterprise.erp.entity.StorageLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StorageLocationRepository
        extends JpaRepository<StorageLocation , Long> {

    //Checks whether a storage-location code already exists within a plant.
    boolean existsByPlant_IdAndStorageLocationCode(
            Long plantId,
            String storageLocationCode
    );

    List<StorageLocation> findByPlant_PlantCode(String plantCode);

    //Finds one storage location using both its plant code and location code.
    Optional<StorageLocation> findByPlant_PlantCodeAndStorageLocationCode(
            String plantCode,
            String storageLocationCode
    );
}
