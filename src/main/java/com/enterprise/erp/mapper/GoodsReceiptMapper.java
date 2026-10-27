
package com.enterprise.erp.mapper;

import com.enterprise.erp.dto.GoodsReceiptItemResponse;
import com.enterprise.erp.dto.GoodsReceiptResponse;
import com.enterprise.erp.entity.GoodsReceipt;
import com.enterprise.erp.entity.GoodsReceiptItem;
import org.springframework.stereotype.Component;

@Component
public class GoodsReceiptMapper {

    public GoodsReceiptResponse toResponse(GoodsReceipt goodsReceipt) {
        GoodsReceiptResponse response = new GoodsReceiptResponse();

        response.setId(goodsReceipt.getId());
        response.setReceiptNumber(goodsReceipt.getReceiptNumber());
        response.setPurchaseOrderNumber(
                goodsReceipt.getPurchaseOrder().getOrderNumber());
        response.setPlantCode(
                goodsReceipt.getPlant().getPlantCode());
        response.setStorageLocationCode(
                goodsReceipt.getStorageLocation().getStorageLocationCode());
        response.setReceiptDate(goodsReceipt.getReceiptDate());

        response.setItems(
                goodsReceipt.getItems()
                        .stream()
                        .map(this::toItemResponse)
                        .toList()
        );

        return response;
    }

    private GoodsReceiptItemResponse toItemResponse(
            GoodsReceiptItem item) {

        GoodsReceiptItemResponse response =
                new GoodsReceiptItemResponse();

        response.setId(item.getId());
        response.setPurchaseOrderItemId(
                item.getPurchaseOrderItem().getId());
        response.setMaterialCode(
                item.getPurchaseOrderItem()
                        .getMaterial().getMaterialCode());
        response.setMaterialDescription(
                item.getPurchaseOrderItem()
                        .getMaterial().getDescription());
        response.setReceivedQuantity(item.getReceivedQuantity());

        return response;
    }
}
