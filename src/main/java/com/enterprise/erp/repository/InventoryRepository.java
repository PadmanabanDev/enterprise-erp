
package com.enterprise.erp.repository;

import com.enterprise.erp.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Find inventory by material code and storage location ID.
    Optional<Inventory> findByMaterial_MaterialCodeAndStorageLocation_Id(
            String materialCode,
            Long storageLocationId
    );

    // Retrieve all inventory records stored at a particular location.
    List<Inventory> findByStorageLocation_Id(Long storageLocationId);

    // Retrieve inventory records for a particular material.
    List<Inventory> findByMaterial_MaterialCode(String materialCode);

    // Check whether inventory exists for a material and storage location.
    boolean existsByMaterial_IdAndStorageLocation_Id(
            Long materialId,
            Long storageLocationId
    );

    // Find inventory by material ID and storage location ID.
    Optional<Inventory> findByMaterial_IdAndStorageLocation_Id(
            Long materialId,
            Long storageLocationId
    );

    // Lock the inventory row until the current transaction completes.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.material.id = :materialId
              AND i.storageLocation.id = :storageLocationId
            """)
    Optional<Inventory> findByMaterialAndLocationForUpdate(
            @Param("materialId") Long materialId,
            @Param("storageLocationId") Long storageLocationId
    );
}
