package com.enterprise.erp.mapper;

import com.enterprise.erp.dto.InventoryRequest;
import com.enterprise.erp.dto.InventoryResponse;
import com.enterprise.erp.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public Inventory toEntity(InventoryRequest request) {
        Inventory inventory = new Inventory();
        inventory.setQuantity(request.getQuantity());
        return inventory;
    }

    public InventoryResponse toResponse(Inventory inventory) {
        InventoryResponse response = new InventoryResponse();

        response.setId(inventory.getId());

        response.setMaterialCode(inventory.getMaterial().getMaterialCode());

        response.setMaterialDescription(inventory.getMaterial().getDescription());

        response.setPlantCode(inventory.getStorageLocation().getPlant().getPlantCode());

        response.setStorageLocationCode(inventory.getStorageLocation().getStorageLocationCode());

        response.setStorageLocationName(inventory.getStorageLocation().getStorageLocationName());

        response.setQuantity(inventory.getQuantity());
        response.setCreatedAt(inventory.getCreatedAt());
        response.setUpdatedAt(inventory.getUpdatedAt());

        return response;
    }
}
