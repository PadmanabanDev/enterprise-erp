package com.enterprise.erp.repository;

import com.enterprise.erp.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends
        JpaRepository<Inventory, Long> {

    Optional<Inventory> findByMaterial_MaterialCodeAndStorageLocation_Id(
            String materialCode,
            Long StorageLocationCode
    );

    List<Inventory> findByStorageLocation_Id(Long storageLocationId);


    List<Inventory> findByMaterial_MaterialCode(String materialCode);

    boolean existsByMaterial_IdAndStorageLocation_Id(
            Long materialId,
            Long storageLocationId
    );


    Optional<Inventory> findByMaterial_IdAndStorageLocation_Id(
            Long materialId,
            Long storageLocationId
    );

}
