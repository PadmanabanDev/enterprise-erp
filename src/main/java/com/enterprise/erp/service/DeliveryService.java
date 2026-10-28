
package com.enterprise.erp.service;

import com.enterprise.erp.dto.CreateDeliveryRequest;
import com.enterprise.erp.dto.DeliveryResponse;
import com.enterprise.erp.entity.*;
import com.enterprise.erp.exception.*;
import com.enterprise.erp.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final OutboundDeliveryRepository deliveryRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final OutboundDeliveryItemRepository deliveryItemRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final InventoryRepository inventoryRepository;
    private record InventoryKey(Long materialId, Long locationId) {}

    // Create a delivery after validating the order, quantities, locations, and stock.
    @Transactional
    public DeliveryResponse createDelivery(CreateDeliveryRequest request) {

        // Reject duplicate delivery numbers.
        if (deliveryRepository.existsByDeliveryNumber(request.deliveryNumber())) {
            throw new DuplicateDeliveryException(
                    "Delivery already exists: " + request.deliveryNumber());
        }

        // Find the sales order or return a not-found error.
        SalesOrder salesOrder = salesOrderRepository
                .findByOrderNumber(request.salesOrderNumber())
                .orElseThrow(() -> new SalesOrderNotFoundException(
                        "Sales order not found: " + request.salesOrderNumber()));

        // Allow delivery creation only for confirmed sales orders.
        if (!"CONFIRMED".equals(salesOrder.getStatus())) {
            throw new InvalidDeliveryException(
                    "Only CONFIRMED sales orders can have deliveries. Current status: "
                            + salesOrder.getStatus());
        }

        // Aggregate requested quantities by sales order item to prevent split-line bypasses.
        Map<Long, BigDecimal> requestedByOrderItem = new LinkedHashMap<>();
        for (CreateDeliveryRequest.DeliveryItemRequest item : request.items()) {
            requestedByOrderItem.merge(
                    item.salesOrderItemId(),
                    item.deliveredQuantity(),
                    BigDecimal::add);
        }

        // Validate order ownership and remaining quantities for every requested order line.
        for (Map.Entry<Long, BigDecimal> entry : requestedByOrderItem.entrySet()) {
            Long orderItemId = entry.getKey();

            SalesOrderItem orderItem = findOrderItem(salesOrder, orderItemId);

            BigDecimal alreadyDelivered = deliveryItemRepository
                    .findBySalesOrderItemId(orderItemId)
                    .stream()
                    .filter(item -> !"CANCELLED".equals(item.getDelivery().getStatus()))
                    .map(OutboundDeliveryItem::getDeliveredQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal remaining = orderItem.getOrderedQuantity()
                    .subtract(alreadyDelivered);

            // Reject quantities greater than the remaining undelivered order quantity.
            if (entry.getValue().compareTo(remaining) > 0) {
                throw new InvalidDeliveryException(
                        "Delivery quantity exceeds remaining quantity for sales order item "
                                + orderItemId + ". Remaining: " + remaining
                                + ", requested: " + entry.getValue());
            }
        }

        // Aggregate quantities by material and storage location for accurate stock checks.
        Map<String, BigDecimal> requestedByStockKey = new LinkedHashMap<>();

        for (CreateDeliveryRequest.DeliveryItemRequest item : request.items()) {
            StorageLocation location = findStorageLocation(
                    item.storageLocationCode(), salesOrder.getPlant().getPlantCode());

            SalesOrderItem orderItem = findOrderItem(salesOrder, item.salesOrderItemId());

            String stockKey = orderItem.getMaterial().getId() + ":" + location.getId();

            requestedByStockKey.merge(
                    stockKey, item.deliveredQuantity(), BigDecimal::add);
        }

        // Reject deliveries that require more stock than the selected locations contain.
        for (Map.Entry<String, BigDecimal> entry : requestedByStockKey.entrySet()) {
            String[] ids = entry.getKey().split(":");
            Long materialId = Long.valueOf(ids[0]);
            Long locationId = Long.valueOf(ids[1]);

            Inventory inventory = inventoryRepository.findByMaterial_MaterialCodeAndStorageLocation_Id(
                            findMaterialCode(salesOrder, materialId), locationId)
                    .orElseThrow(() -> new InsufficientDeliveryInventoryException(
                            "No inventory record exists for material ID "
                                    + materialId + " at storage location ID " + locationId));

            if (inventory.getQuantity().compareTo(entry.getValue()) < 0) {
                throw new InsufficientDeliveryInventoryException(
                        "Insufficient inventory at storage location for material "
                                + findMaterialCode(salesOrder, materialId)
                                + ". Available: " + inventory.getQuantity()
                                + ", requested: " + entry.getValue());
            }
        }

        // Initialize the delivery header without changing inventory.
        OutboundDelivery delivery = new OutboundDelivery();
        delivery.setDeliveryNumber(request.deliveryNumber());
        delivery.setSalesOrder(salesOrder);
        delivery.setStatus("CREATED");

        // Build delivery item lines using validated order items and locations.
        List<OutboundDeliveryItem> deliveryItems = new ArrayList<>();

        for (CreateDeliveryRequest.DeliveryItemRequest itemRequest : request.items()) {
            SalesOrderItem orderItem = findOrderItem(
                    salesOrder, itemRequest.salesOrderItemId());

            StorageLocation location = findStorageLocation(
                    itemRequest.storageLocationCode(),
                    salesOrder.getPlant().getPlantCode());

            OutboundDeliveryItem deliveryItem = new OutboundDeliveryItem();
            deliveryItem.setDelivery(delivery);
            deliveryItem.setSalesOrderItem(orderItem);
            deliveryItem.setMaterial(orderItem.getMaterial());
            deliveryItem.setStorageLocation(location);
            deliveryItem.setDeliveredQuantity(itemRequest.deliveredQuantity());

            deliveryItems.add(deliveryItem);
        }

        // Attach the item lines and persist the delivery atomically.
        delivery.getItems().addAll(deliveryItems);
        OutboundDelivery savedDelivery = deliveryRepository.save(delivery);

        // Return the saved delivery as a response DTO.
        return toResponse(savedDelivery);
    }

    // Find an order item and ensure it belongs to the supplied sales order.
    private SalesOrderItem findOrderItem(SalesOrder salesOrder, Long orderItemId) {
        return salesOrder.getItems().stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new InvalidDeliveryException(
                        "Sales order item does not belong to order: " + orderItemId));
    }

    // Resolve a storage location only when it belongs to the selected plant.
    private StorageLocation findStorageLocation(String locationCode, String plantCode) {
        return storageLocationRepository
                .findByStorageLocationCodeAndPlant_PlantCode(locationCode, plantCode)
                .orElseThrow(() -> new InvalidDeliveryException(
                        "Storage location not found in sales order plant: " + locationCode));
    }

    // Resolve a material code from the sales order items using the material database ID.
    private String findMaterialCode(SalesOrder salesOrder, Long materialId) {
        return salesOrder.getItems().stream()
                .map(SalesOrderItem::getMaterial)
                .filter(material -> material.getId().equals(materialId))
                .map(Material::getMaterialCode)
                .findFirst()
                .orElseThrow(() -> new InvalidDeliveryException(
                        "Material does not belong to the sales order: " + materialId));
    }

    // Retrieve a delivery by its unique delivery number.
    @Transactional
    public DeliveryResponse getDeliveryByNumber(String deliveryNumber) {
        OutboundDelivery delivery = deliveryRepository
                .findByDeliveryNumber(deliveryNumber)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery not found: " + deliveryNumber));

        return toResponse(delivery);
    }

    // Retrieve all deliveries as response DTOs.
    @Transactional
    public List<DeliveryResponse> getAllDeliveries() {
        return deliveryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Convert a delivery entity and its items into the API response DTO.
    private DeliveryResponse toResponse(OutboundDelivery delivery) {
        List<DeliveryResponse.DeliveryItemResponse> items =
                delivery.getItems().stream()
                        .map(item -> new DeliveryResponse.DeliveryItemResponse(
                                item.getId(),
                                item.getSalesOrderItem().getId(),
                                item.getMaterial().getMaterialCode(),
                                item.getMaterial().getDescription(),
                                item.getStorageLocation().getStorageLocationCode(),
                                item.getDeliveredQuantity()))
                        .toList();

        return new DeliveryResponse(
                delivery.getId(),
                delivery.getDeliveryNumber(),
                delivery.getSalesOrder().getOrderNumber(),
                delivery.getStatus(),
                delivery.getCreatedAt(),
                delivery.getUpdatedAt(),
                items);
    }

    @Transactional
    public DeliveryResponse postGoodsIssue(String deliveryNumber) {

        // 1. Find the delivery.
        OutboundDelivery delivery = deliveryRepository
                .findByDeliveryNumber(deliveryNumber)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery not found: " + deliveryNumber
                ));

        // 2. Only CREATED deliveries can be posted.
        if (!"CREATED".equals(delivery.getStatus())) {
            throw new InvalidDeliveryException(
                    "Goods issue can only be posted for CREATED deliveries. "
                            + "Current status: " + delivery.getStatus()
            );
        }

        // 3. Aggregate quantities by material and storage location.
        Map<InventoryKey, BigDecimal> requiredQuantities =
                new LinkedHashMap<>();

        for (OutboundDeliveryItem item : delivery.getItems()) {

            InventoryKey key = new InventoryKey(
                    item.getMaterial().getId(),
                    item.getStorageLocation().getId()
            );

            requiredQuantities.merge(
                    key,
                    item.getDeliveredQuantity(),
                    BigDecimal::add
            );
        }

        // 4. Sort inventory keys to reduce the risk of deadlocks.
        List<InventoryKey> sortedKeys = new ArrayList<>(
                requiredQuantities.keySet()
        );

        sortedKeys.sort(
                java.util.Comparator
                        .comparing(InventoryKey::materialId)
                        .thenComparing(InventoryKey::locationId)
        );

        // 5. Lock all required inventory rows.
        Map<InventoryKey, Inventory> lockedInventory =
                new LinkedHashMap<>();

        for (InventoryKey key : sortedKeys) {

            Inventory inventory = inventoryRepository
                    .findByMaterialAndLocationForUpdate(
                            key.materialId(),
                            key.locationId()
                    )
                    .orElseThrow(() ->
                            new InsufficientDeliveryInventoryException(
                                    "No inventory record exists for material ID "
                                            + key.materialId()
                                            + " at storage location ID "
                                            + key.locationId()
                            )
                    );

            lockedInventory.put(key, inventory);
        }

        // 6. Validate every required quantity before changing any stock.
        for (InventoryKey key : sortedKeys) {

            Inventory inventory = lockedInventory.get(key);
            BigDecimal requiredQuantity = requiredQuantities.get(key);

            if (inventory.getQuantity().compareTo(requiredQuantity) < 0) {
                throw new InsufficientDeliveryInventoryException(
                        "Insufficient inventory for material ID "
                                + key.materialId()
                                + " at storage location ID "
                                + key.locationId()
                                + ". Available: "
                                + inventory.getQuantity()
                                + ", required: "
                                + requiredQuantity
                );
            }
        }

        // 7. Deduct inventory after all validations pass.
        for (InventoryKey key : sortedKeys) {

            Inventory inventory = lockedInventory.get(key);
            BigDecimal requiredQuantity = requiredQuantities.get(key);

            inventory.setQuantity(
                    inventory.getQuantity().subtract(requiredQuantity)
            );
        }

        // 8. Mark the delivery as POSTED.
        delivery.setStatus("POSTED");

        // 9. Save and return the updated delivery.
        OutboundDelivery savedDelivery =
                deliveryRepository.save(delivery);

        return toResponse(savedDelivery);
    }


    @Transactional
    public DeliveryResponse cancelDelivery(String deliveryNumber) {

        // 1. Find the delivery.
        OutboundDelivery delivery = deliveryRepository
                .findByDeliveryNumber(deliveryNumber)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery not found: " + deliveryNumber
                ));

        // 2. Only CREATED deliveries can be cancelled.
        if (!"CREATED".equals(delivery.getStatus())) {
            throw new InvalidDeliveryException(
                    "Only CREATED deliveries can be cancelled. "
                            + "Current status: " + delivery.getStatus()
            );
        }

        // 3. Update the delivery status.
        delivery.setStatus("CANCELLED");

        // 4. Save the updated delivery.
        OutboundDelivery cancelledDelivery =
                deliveryRepository.save(delivery);

        // 5. Return the updated response.
        return toResponse(cancelledDelivery);
    }

}
