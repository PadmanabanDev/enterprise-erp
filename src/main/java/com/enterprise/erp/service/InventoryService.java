package com.enterprise.erp.service;

import com.enterprise.erp.dto.InventoryQuantityUpdateRequest;
import com.enterprise.erp.dto.InventoryRequest;
import com.enterprise.erp.dto.InventoryResponse;
import com.enterprise.erp.entity.Inventory;
import com.enterprise.erp.entity.Material;
import com.enterprise.erp.entity.StorageLocation;
import com.enterprise.erp.exception.DuplicateInventoryException;
import com.enterprise.erp.exception.InventoryNotFoundException;
import com.enterprise.erp.exception.MaterialNotFoundException;
import com.enterprise.erp.exception.StorageLocationNotFoundException;
import com.enterprise.erp.mapper.InventoryMapper;
import com.enterprise.erp.repository.InventoryRepository;
import com.enterprise.erp.repository.MaterialRepository;
import com.enterprise.erp.repository.StorageLocationRepository;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private  final StorageLocationRepository storageLocationRepository;
    private final MaterialRepository materialRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryResponse createInventory(InventoryRequest request){

        //Find Material code
        Material material = materialRepository.findByMaterialCode(request.getMaterialCode())
                .orElseThrow(()->new MaterialNotFoundException(
                        "Material not found: "+ request.getMaterialCode()
                ));

        StorageLocation storageLocation =  storageLocationRepository.findByPlant_PlantCodeAndStorageLocationCode(
                request.getPlantCode(),request.getStorageLocationCode())
                .orElseThrow(()-> new StorageLocationNotFoundException(
                        "Storage location "
                        + request.getStorageLocationCode()
                        +" Not found in plant "
                        +request.getPlantCode()
                ));
        if(inventoryRepository.existsByMaterial_IdAndStorageLocation_Id(
                material.getId(), storageLocation.getId()
        )){
            throw new DuplicateInventoryException(
                    "Inventory already exists for material "
                            + request.getMaterialCode()
                            + " at storage location "
                            + request.getStorageLocationCode()
                            + " in plant " + request.getPlantCode());

        }
        Inventory inventory = inventoryMapper.toEntity(request);
        inventory.setMaterial(material);
        inventory.setStorageLocation(storageLocation);

        Inventory saved = inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(saved);

    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryByStorageLocation(
            String plantCode,
            String storageLocationCode) {

        StorageLocation storageLocation = storageLocationRepository
                .findByPlant_PlantCodeAndStorageLocationCode(
                        plantCode, storageLocationCode)
                .orElseThrow(() -> new StorageLocationNotFoundException(
                        "Storage location " + storageLocationCode
                                + " not found in plant " + plantCode));

        return inventoryRepository
                .findByStorageLocation_Id(storageLocation.getId())
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryByMaterial(String materialCode) {

        if (!materialRepository.existsByMaterialCode(materialCode)) {
            throw new MaterialNotFoundException(
                    "Material not found: " + materialCode);
        }

        return inventoryRepository.findByMaterial_MaterialCode(materialCode)
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }



    @Transactional
    public InventoryResponse updateInventoryQuantity(
            Long inventoryId,
            InventoryQuantityUpdateRequest request) {

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found with id: " + inventoryId));

        inventory.setQuantity(request.getQuantity());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toResponse(updatedInventory);
    }


    //Get inventory by ID
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryById(Long inventoryId) {

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found with id: " + inventoryId));

        return inventoryMapper.toResponse(inventory);
    }

    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll()
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }


}

