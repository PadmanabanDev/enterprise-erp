
package com.enterprise.erp.service;

import com.enterprise.erp.dto.GoodsReceiptItemRequest;
import com.enterprise.erp.dto.GoodsReceiptRequest;
import com.enterprise.erp.dto.GoodsReceiptResponse;
import com.enterprise.erp.entity.GoodsReceipt;
import com.enterprise.erp.entity.GoodsReceiptItem;
import com.enterprise.erp.entity.Inventory;
import com.enterprise.erp.entity.Plant;
import com.enterprise.erp.entity.PurchaseOrder;
import com.enterprise.erp.entity.PurchaseOrderItem;
import com.enterprise.erp.entity.StorageLocation;
import com.enterprise.erp.exception.*;
import com.enterprise.erp.mapper.GoodsReceiptMapper;
import com.enterprise.erp.repository.GoodsReceiptRepository;
import com.enterprise.erp.repository.InventoryRepository;
import com.enterprise.erp.repository.PlantRepository;
import com.enterprise.erp.repository.PurchaseOrderItemRepository;
import com.enterprise.erp.repository.PurchaseOrderRepository;
import com.enterprise.erp.repository.StorageLocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class GoodsReceiptService {

    private final GoodsReceiptRepository goodsReceiptRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final PlantRepository plantRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final InventoryRepository inventoryRepository;
    private final GoodsReceiptMapper goodsReceiptMapper;



    // One transaction keeps the receipt, inventory changes, and PO status
    // consistent. If a runtime exception occurs, database changes roll back.
    @Transactional
    public GoodsReceiptResponse createGoodsReceipt(
            GoodsReceiptRequest request) {

        // 1. Prevent duplicate receipt numbers.
        // Each receipt must have its own unique business reference.
        if (goodsReceiptRepository.existsByReceiptNumber(
                request.getReceiptNumber())) {

            throw new DuplicateGoodsReceiptException(
                     request.getReceiptNumber());
        }

        // 2. Find the purchase order against which goods are received.
        // A nonexistent order cannot have a valid goods receipt.
        PurchaseOrder purchaseOrder = purchaseOrderRepository
                .findByOrderNumber(request.getPurchaseOrderNumber())
                .orElseThrow(() -> new PurchaseOrderNotFoundException(
                        "Purchase order not found: "
                                + request.getPurchaseOrderNumber()));

        // 3. Only approved or partially received orders can receive goods.
        // CREATED, CANCELLED, and COMPLETED orders are not eligible.
        String currentStatus = purchaseOrder.getStatus();

        if (!"APPROVED".equals(currentStatus)
                && !"PARTIALLY_RECEIVED".equals(currentStatus)) {

            throw new InvalidPurchaseOrderStatusException(
                    "Goods can only be received for APPROVED or "
                            + "PARTIALLY_RECEIVED purchase orders. "
                            + "Current status: " + currentStatus);
        }

        // 4. Find the plant specified in the request.
        Plant plant = plantRepository
                .findByPlantCode(request.getPlantCode())
                .orElseThrow(() -> new PlantNotFoundException(
                        "Plant not found: " + request.getPlantCode()));

        // 5. Ensure the plant matches the purchase order's plant.
        // This prevents receiving an order at an unrelated plant.
        if (!plant.getId().equals(purchaseOrder.getPlant().getId())) {
            throw new GoodsReceiptPlantMismatchException();
        }

        // 6. Find the storage location within the requested plant.This also ensures the location belongs to that plant.
        StorageLocation storageLocation = storageLocationRepository
                .findByPlant_PlantCodeAndStorageLocationCode(
                        request.getPlantCode(),
                        request.getStorageLocationCode())
                .orElseThrow(() -> new StorageLocationNotFoundException(
                        "Storage location not found: "
                                + request.getStorageLocationCode()
                                + " in plant " + request.getPlantCode()));

        // 7. Reject duplicate purchase order item IDs in the same receipt Otherwise, two lines could each pass validation against the same
        // outstanding quantity and together receive too much stock.
        Set<Long> itemIds = new HashSet<>();

        for (GoodsReceiptItemRequest itemRequest : request.getItems()) {
            if (!itemIds.add(itemRequest.getPurchaseOrderItemId())) {
                throw new DuplicateGoodsReceiptItemException(
                        itemRequest.getPurchaseOrderItemId());
            }
        }

        // 8. Build the receipt header.
        // Items will be attached below using addItem(), which sets both sides of the GoodsReceipt-GoodsReceiptItem relationship.
        GoodsReceipt goodsReceipt = new GoodsReceipt();
        goodsReceipt.setReceiptNumber(request.getReceiptNumber());
        goodsReceipt.setPurchaseOrder(purchaseOrder);
        goodsReceipt.setPlant(plant);
        goodsReceipt.setStorageLocation(storageLocation);

        // 9. Validate each item, create receipt lines, and update stock.
        for (GoodsReceiptItemRequest itemRequest : request.getItems()) {

            // Find the purchase order line selected by the request.
            PurchaseOrderItem purchaseOrderItem =
                    purchaseOrderItemRepository
                            .findById(itemRequest.getPurchaseOrderItemId())
                            .orElseThrow(() -> new PurchaseOrderItemNotFoundException(
                                    itemRequest.getPurchaseOrderItemId()));

            // A line must belong to this exact purchase order.
            // This prevents receiving an item from a different order.
            if (!purchaseOrderItem.getPurchaseOrder().getId()
                    .equals(purchaseOrder.getId())) {

                throw new PurchaseOrderItemMismatchException(
                        purchaseOrderItem.getId(),
                        purchaseOrder.getOrderNumber()
                );
            }

            // Calculate the quantity received on earlier receipts.
            // The current receipt has not been saved yet, so this is the
            // quantity received before processing this request.
            BigDecimal alreadyReceived =
                    goodsReceiptRepository
                            .sumReceivedQuantityByPurchaseOrderItemId(
                                    purchaseOrderItem.getId());

            // Outstanding = ordered quantity - previously received quantity.
            BigDecimal outstandingQuantity =
                    purchaseOrderItem.getOrderedQuantity()
                            .subtract(alreadyReceived);

            // Never allow receiving more than the remaining quantity.
            if (itemRequest.getReceivedQuantity().compareTo(outstandingQuantity) > 0) {
                throw new ExcessGoodsReceiptQuantityException(
                        purchaseOrderItem.getId(),
                        outstandingQuantity
                );
            }


            // Create a receipt line for the quantity physically received.
            GoodsReceiptItem receiptItem = new GoodsReceiptItem();
            receiptItem.setPurchaseOrderItem(purchaseOrderItem);
            receiptItem.setReceivedQuantity(
                    itemRequest.getReceivedQuantity());

            // Keeps the receipt's item list and the item's parent reference
            // synchronized for JPA persistence.
            goodsReceipt.addItem(receiptItem);

            // Find existing stock for this material at this location.
            // If no record exists, create a new inventory record starting at 0.
            Inventory inventory = inventoryRepository
                    .findByMaterial_IdAndStorageLocation_Id(
                            purchaseOrderItem.getMaterial().getId(),
                            storageLocation.getId())
                    .orElseGet(() -> {
                        Inventory newInventory = new Inventory();
                        newInventory.setMaterial(
                                purchaseOrderItem.getMaterial());
                        newInventory.setStorageLocation(storageLocation);
                        newInventory.setQuantity(BigDecimal.ZERO);
                        return newInventory;
                    });

            // Add the received quantity to the current stock.
            inventory.setQuantity(
                    inventory.getQuantity().add(
                            itemRequest.getReceivedQuantity()));

            // Save the stock change. The surrounding transaction ensures
            // this change rolls back if receipt processing later fails.
            inventoryRepository.save(inventory);
        }

        // 10. Save the receipt header and all receipt lines.
        // GoodsReceipt uses CascadeType.ALL for its items.
        GoodsReceipt savedReceipt =
                goodsReceiptRepository.save(goodsReceipt);

        // 11. Check every purchase order line after saving the receipt.
        // The aggregate query now includes quantities from this receipt.
        boolean allItemsFullyReceived =
                purchaseOrder.getItems().stream()
                        .allMatch(poItem -> {
                            BigDecimal totalReceived =
                                    goodsReceiptRepository
                                            .sumReceivedQuantityByPurchaseOrderItemId(
                                                    poItem.getId());

                            return totalReceived.compareTo(
                                    poItem.getOrderedQuantity()) >= 0;
                        });

        // 12. Update the purchase order's overall status.
        // It is COMPLETED only when every line has been fully received.
        if (allItemsFullyReceived) {
            purchaseOrder.setStatus("COMPLETED");
        } else {
            purchaseOrder.setStatus("PARTIALLY_RECEIVED");
        }

        purchaseOrderRepository.save(purchaseOrder);

        // 13. Return the saved receipt as a response DTO.
        return goodsReceiptMapper.toResponse(savedReceipt);
    }

    @Transactional(readOnly = true)
    public List<GoodsReceiptResponse> getAllGoodsReceipts() {

        return goodsReceiptRepository.findAll()
                .stream()
                .map(goodsReceiptMapper::toResponse)
                .toList();
    }



    @Transactional(readOnly = true)
    public GoodsReceiptResponse getGoodsReceiptByReceiptNumber(String receiptNumber) {

        GoodsReceipt goodsReceipt = goodsReceiptRepository
                .findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new GoodsReceiptNotFoundException(receiptNumber));

        return goodsReceiptMapper.toResponse(goodsReceipt);
    }



}
