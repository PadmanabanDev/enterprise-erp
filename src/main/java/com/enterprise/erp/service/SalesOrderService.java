
package com.enterprise.erp.service;

import com.enterprise.erp.dto.CreateSalesOrderRequest;
import com.enterprise.erp.dto.SalesOrderResponse;
import com.enterprise.erp.entity.*;
import com.enterprise.erp.exception.*;
import com.enterprise.erp.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final BusinessPartnerRepository businessPartnerRepository;
    private final MaterialRepository materialRepository;
    private final PlantRepository plantRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public SalesOrderResponse createSalesOrder(
            CreateSalesOrderRequest request) {

        // 1. Check for duplicate order number
        if (salesOrderRepository.existsByOrderNumber(
                request.orderNumber())) {
            throw new DuplicateSalesOrderException(
                    "Sales order already exists: "
                            + request.orderNumber());
        }

        // 2. Find and validate customer
        BusinessPartner customer = businessPartnerRepository
                .findByPartnerCode(request.customerCode())
                .orElseThrow(() -> new SalesOrderCustomerNotFoundException(
                        "Customer not found: "
                                + request.customerCode()));

        if (!"CUSTOMER".equalsIgnoreCase(customer.getPartnerType())) {
            throw new InvalidSalesOrderCustomerException(
                    "Business partner is not a customer: "
                            + request.customerCode());
        }

        // 3. Find plant
        Plant plant = plantRepository
                .findByPlantCode(request.plantCode())
                .orElseThrow(() -> new SalesOrderPlantNotFoundException(
                        "Plant not found: " + request.plantCode()));

        // 4. Validate combined inventory demand before building the order
        validateInventoryAvailability(request);

        // 5. Create sales order
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setOrderNumber(request.orderNumber());
        salesOrder.setCustomer(customer);
        salesOrder.setPlant(plant);
        salesOrder.setStatus("CREATED");
        salesOrder.setTotalAmount(BigDecimal.ZERO);

        BigDecimal orderTotal = BigDecimal.ZERO;
        List<SalesOrderItem> items = new ArrayList<>();

        // 6. Build order items and calculate totals
        for (CreateSalesOrderRequest.SalesOrderItemRequest itemRequest
                : request.items()) {

            Material material = materialRepository
                    .findByMaterialCode(itemRequest.materialCode())
                    .orElseThrow(() -> new SalesOrderMaterialNotFoundException(
                            "Material not found: "
                                    + itemRequest.materialCode()));

            BigDecimal quantity = itemRequest.orderedQuantity();

            BigDecimal unitPrice = itemRequest.unitPrice()
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal lineTotal = quantity.multiply(unitPrice)
                    .setScale(2, RoundingMode.HALF_UP);

            SalesOrderItem item = new SalesOrderItem();
            item.setSalesOrder(salesOrder);
            item.setMaterial(material);
            item.setOrderedQuantity(quantity);
            item.setUnitPrice(unitPrice);
            item.setLineTotal(lineTotal);

            items.add(item);
            orderTotal = orderTotal.add(lineTotal);
        }

        salesOrder.setTotalAmount(
                orderTotal.setScale(2, RoundingMode.HALF_UP));

        salesOrder.getItems().addAll(items);

        SalesOrder savedOrder = salesOrderRepository.save(salesOrder);

        return toResponse(savedOrder);
    }

    // Calculate stock across all storage locations belonging to the plant
    private BigDecimal getAvailableQuantity(
            String materialCode,
            String plantCode) {

        List<StorageLocation> storageLocations =
                storageLocationRepository
                        .findByPlant_PlantCode(plantCode);

        BigDecimal availableQuantity = BigDecimal.ZERO;

        for (StorageLocation location : storageLocations) {

            var inventory = inventoryRepository
                    .findByMaterial_MaterialCodeAndStorageLocation_Id(
                            materialCode,
                            location.getId());

            if (inventory.isPresent()) {
                availableQuantity = availableQuantity.add(
                        inventory.get().getQuantity());
            }
        }

        return availableQuantity;
    }

    // Combine repeated material lines before checking available stock
    private void validateInventoryAvailability(
            CreateSalesOrderRequest request) {

        Map<String, BigDecimal> requestedQuantities =
                new LinkedHashMap<>();

        for (CreateSalesOrderRequest.SalesOrderItemRequest item
                : request.items()) {

            requestedQuantities.merge(
                    item.materialCode(),
                    item.orderedQuantity(),
                    BigDecimal::add);
        }

        for (Map.Entry<String, BigDecimal> entry
                : requestedQuantities.entrySet()) {

            String materialCode = entry.getKey();
            BigDecimal totalRequested = entry.getValue();

            BigDecimal availableQuantity = getAvailableQuantity(
                    materialCode,
                    request.plantCode());

            if (availableQuantity.compareTo(totalRequested) < 0) {
                throw new InsufficientInventoryException(
                        "Insufficient inventory for material: "
                                + materialCode
                                + ". Available: " + availableQuantity
                                + ", requested: " + totalRequested);
            }
        }
    }

    private SalesOrderResponse toResponse(SalesOrder order) {

        List<SalesOrderResponse.SalesOrderItemResponse> itemResponses =
                order.getItems().stream()
                        .map(item ->
                                new SalesOrderResponse.SalesOrderItemResponse(
                                        item.getId(),
                                        item.getMaterial().getMaterialCode(),
                                        item.getMaterial().getDescription(),
                                        item.getOrderedQuantity(),
                                        item.getUnitPrice(),
                                        item.getLineTotal()))
                        .toList();

        return new SalesOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getPartnerCode(),
                order.getCustomer().getName(),
                order.getPlant().getPlantCode(),
                order.getPlant().getPlantName(),
                order.getStatus(),
                order.getOrderDate(),
                order.getTotalAmount(),
                itemResponses);
    }

    @Transactional
    public SalesOrderResponse getSalesOrderByOrderNumber(
            String orderNumber) {

        SalesOrder order = salesOrderRepository
                .findByOrderNumber(orderNumber)
                .orElseThrow(() -> new SalesOrderNotFoundException(
                        "Sales order not found: " + orderNumber));

        return toResponse(order);
    }

    @Transactional
    public List<SalesOrderResponse> getAllSalesOrders() {
        return salesOrderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SalesOrderResponse updateSalesOrderStatus(
            String orderNumber,
            String requestedStatus) {

        SalesOrder order = salesOrderRepository
                .findByOrderNumber(orderNumber)
                .orElseThrow(() -> new SalesOrderNotFoundException(
                        "Sales order not found: " + orderNumber));

        if (!"CREATED".equals(order.getStatus())) {
            throw new InvalidSalesOrderStatusException(
                    "Only sales orders with CREATED status can be confirmed. "
                            + "Current status: " + order.getStatus());
        }

        if (!"CONFIRMED".equalsIgnoreCase(requestedStatus)) {
            throw new InvalidSalesOrderStatusException(
                    "Only the CONFIRMED status is allowed from CREATED.");
        }

        order.setStatus("CONFIRMED");

        SalesOrder savedOrder = salesOrderRepository.save(order);

        return toResponse(savedOrder);
    }

    @Transactional
    public SalesOrderResponse cancelSalesOrder(String orderNumber) {

        SalesOrder order = salesOrderRepository
                .findByOrderNumber(orderNumber)
                .orElseThrow(() -> new SalesOrderNotFoundException(
                        "Sales order not found: " + orderNumber));

        if (!"CREATED".equals(order.getStatus())
                && !"CONFIRMED".equals(order.getStatus())) {
            throw new InvalidSalesOrderStatusException(
                    "Only sales orders with CREATED or CONFIRMED status "
                            + "can be cancelled. Current status: "
                            + order.getStatus());
        }

        order.setStatus("CANCELLED");

        SalesOrder savedOrder = salesOrderRepository.save(order);

        return toResponse(savedOrder);
    }
}
