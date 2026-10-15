
package com.enterprise.erp.controller;

import com.enterprise.erp.dto.InventoryQuantityUpdateRequest;
import com.enterprise.erp.dto.InventoryRequest;
import com.enterprise.erp.dto.InventoryResponse;
import com.enterprise.erp.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public InventoryResponse createInventory(
            @Valid @RequestBody InventoryRequest request) {

        return inventoryService.createInventory(request);
    }

    @GetMapping("/plant/{plantCode}/storage-location/{storageLocationCode}")
    public List<InventoryResponse> getInventoryByStorageLocation(
            @PathVariable String plantCode,
            @PathVariable String storageLocationCode) {

        return inventoryService.getInventoryByStorageLocation(
                plantCode, storageLocationCode);
    }

    @GetMapping("/material/{materialCode}")
    public List<InventoryResponse> getInventoryByMaterial(
            @PathVariable String materialCode) {

        return inventoryService.getInventoryByMaterial(materialCode);
    }


    @PutMapping("/{inventoryId}/quantity")
    public InventoryResponse updateInventoryQuantity(
            @PathVariable Long inventoryId,
            @Valid @RequestBody InventoryQuantityUpdateRequest request) {

        return inventoryService.updateInventoryQuantity(
                inventoryId, request);
    }

}
