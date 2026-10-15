package com.enterprise.erp.mapper;

import com.enterprise.erp.dto.PurchaseOrderItemResponse;
import com.enterprise.erp.dto.PurchaseOrderResponse;
import com.enterprise.erp.entity.PurchaseOrder;
import com.enterprise.erp.entity.PurchaseOrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PurchaseOrderMapper {

    public PurchaseOrderResponse toResponse(PurchaseOrder purchaseOrder) {

        PurchaseOrderResponse response = new PurchaseOrderResponse();

        response.setId(purchaseOrder.getId());response.setOrderNumber(purchaseOrder.getOrderNumber());
        response.setSupplierCode(purchaseOrder.getSupplier().getPartnerCode());
        response.setSupplierName(purchaseOrder.getSupplier().getName());
        response.setPlantCode(purchaseOrder.getPlant().getPlantCode());
        response.setPlantName(purchaseOrder.getPlant().getPlantName());
        response.setStatus(purchaseOrder.getStatus());
        response.setOrderDate(purchaseOrder.getOrderDate());
        response.setItems(purchaseOrder.getItems()
                        .stream()
                        .map(this::toItemResponse)
                        .toList()
        );

        return response;
    }

    private PurchaseOrderItemResponse toItemResponse(
            PurchaseOrderItem item) {

        PurchaseOrderItemResponse response =
                new PurchaseOrderItemResponse();

        response.setId(item.getId());

        response.setMaterialCode(item.getMaterial().getMaterialCode());
        response.setMaterialDescription(item.getMaterial().getDescription());
        response.setOrderedQuantity(item.getOrderedQuantity());
        response.setUnitPrice(item.getUnitPrice());
        BigDecimal lineTotal = item.getOrderedQuantity().multiply(item.getUnitPrice());
        response.setLineTotal(lineTotal);

        return response;
    }
}
