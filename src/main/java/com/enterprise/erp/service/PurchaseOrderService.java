
package com.enterprise.erp.service;

import com.enterprise.erp.dto.PurchaseOrderRequest;
import com.enterprise.erp.dto.PurchaseOrderResponse;
import com.enterprise.erp.entity.BusinessPartner;
import com.enterprise.erp.entity.Material;
import com.enterprise.erp.entity.Plant;
import com.enterprise.erp.entity.PurchaseOrder;
import com.enterprise.erp.entity.PurchaseOrderItem;
import com.enterprise.erp.exception.*;
import com.enterprise.erp.mapper.PurchaseOrderMapper;
import com.enterprise.erp.repository.BusinessPartnerRepository;
import com.enterprise.erp.repository.MaterialRepository;
import com.enterprise.erp.repository.PlantRepository;
import com.enterprise.erp.repository.PurchaseOrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final BusinessPartnerRepository businessPartnerRepository;
    private final PlantRepository plantRepository;
    private final MaterialRepository materialRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;



    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request) {

        // 1. Check whether the order number already exists
        if (purchaseOrderRepository.existsByOrderNumber(request.getOrderNumber())) {
            throw new DuplicatePurchaseOrderException(
                    "Purchase order already exists: " + request.getOrderNumber());
        }

        // 2. Find the supplier
        BusinessPartner supplier = businessPartnerRepository
                .findByPartnerCode(request.getSupplierCode())
                .orElseThrow(() -> new BusinessPartnerNotFoundException(
                        "Business partner not found: " + request.getSupplierCode()));

        // 3. Make sure the business partner is actually a supplier
        if (!"SUPPLIER".equalsIgnoreCase(supplier.getPartnerType())) {
            throw new IllegalArgumentException(
                    "Business partner " + request.getSupplierCode()
                            + " is not a supplier");
        }

        // 4. Find the plant
        Plant plant = plantRepository
                .findByPlantCode(request.getPlantCode())
                .orElseThrow(() -> new PlantNotFoundException(
                        "Plant not found: " + request.getPlantCode()));

        // 5. Create the purchase order header
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setOrderNumber(request.getOrderNumber());
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setPlant(plant);
        purchaseOrder.setStatus("CREATED");

        // 6. Create each purchase order item
        for (var itemRequest : request.getItems()) {

            Material material = materialRepository
                    .findByMaterialCode(itemRequest.getMaterialCode())
                    .orElseThrow(() -> new MaterialNotFoundException(
                            "Material not found: " + itemRequest.getMaterialCode()));

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setMaterial(material);
            item.setOrderedQuantity(itemRequest.getOrderedQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());

            // Sets both sides of the relationship
            purchaseOrder.addItem(item);
        }

        // 7. Save header and items together
        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);

        // 8. Convert the saved entity into the API response
        return purchaseOrderMapper.toResponse(savedOrder);
    }


    @Transactional(readOnly = true)
    public PurchaseOrderResponse getPurchaseOrderByOrderNumber(
            String orderNumber) {

        PurchaseOrder purchaseOrder = purchaseOrderRepository
                .findByOrderNumber(orderNumber)
                .orElseThrow(() -> new PurchaseOrderNotFoundException(
                        "Purchase order not found: " + orderNumber));

        return purchaseOrderMapper.toResponse(purchaseOrder);
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll()
                .stream()
                .map(purchaseOrderMapper::toResponse)
                .toList();
    }

}
