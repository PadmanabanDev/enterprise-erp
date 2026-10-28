
package com.enterprise.erp.service;

import com.enterprise.erp.dto.CreateSalesOrderRequest;
import com.enterprise.erp.dto.SalesOrderResponse;
import com.enterprise.erp.entity.BusinessPartner;
import com.enterprise.erp.entity.Material;
import com.enterprise.erp.entity.Plant;
import com.enterprise.erp.entity.SalesOrder;
import com.enterprise.erp.entity.SalesOrderItem;
import com.enterprise.erp.entity.StorageLocation;
import com.enterprise.erp.exception.DuplicateSalesOrderException;
import com.enterprise.erp.exception.InsufficientInventoryException;
import com.enterprise.erp.exception.InvalidSalesOrderCustomerException;
import com.enterprise.erp.exception.InvalidSalesOrderStatusException;
import com.enterprise.erp.exception.SalesOrderCustomerNotFoundException;
import com.enterprise.erp.exception.SalesOrderMaterialNotFoundException;
import com.enterprise.erp.exception.SalesOrderNotFoundException;
import com.enterprise.erp.exception.SalesOrderPlantNotFoundException;
import com.enterprise.erp.repository.BusinessPartnerRepository;
import com.enterprise.erp.repository.InventoryRepository;
import com.enterprise.erp.repository.MaterialRepository;
import com.enterprise.erp.repository.PlantRepository;
import com.enterprise.erp.repository.SalesOrderRepository;
import com.enterprise.erp.repository.StorageLocationRepository;
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

    // Validate the request, check stock, calculate totals, and create a sales order.
    @Transactional
    public SalesOrderResponse createSalesOrder(CreateSalesOrderRequest request) {

        // Reject duplicate sales order numbers.
        if (salesOrderRepository.existsByOrderNumber(request.orderNumber())) {
            throw new DuplicateSalesOrderException(
                    "Sales order already exists: " + request.orderNumber());
        }

        // Find the requested customer or return a not-found error.
        BusinessPartner customer = businessPartnerRepository
                .findByPartnerCode(request.customerCode())
                .orElseThrow(() -> new SalesOrderCustomerNotFoundException(
                        "Customer not found: " + request.customerCode()));

        // Ensure the selected business partner is a customer, not a supplier.
        if (!"CUSTOMER".equalsIgnoreCase(customer.getPartnerType())) {
            throw new InvalidSalesOrderCustomerException(
                    "Business partner is not a customer: "
                            + request.customerCode());
        }

        // Find the plant where the sales order will be fulfilled.
        Plant plant = plantRepository
                .findByPlantCode(request.plantCode())
                .orElseThrow(() -> new SalesOrderPlantNotFoundException(
                        "Plant not found: " + request.plantCode()));

        // Load each distinct material once and reject unknown material codes.
        Map<String, Material> materialsByCode = new LinkedHashMap<>();

        for (CreateSalesOrderRequest.SalesOrderItemRequest itemRequest
                : request.items()) {

            String materialCode = itemRequest.materialCode();

            if (!materialsByCode.containsKey(materialCode)) {
                Material material = materialRepository
                        .findByMaterialCode(materialCode)
                        .orElseThrow(() -> new SalesOrderMaterialNotFoundException(
                                "Material not found: " + materialCode));

                materialsByCode.put(materialCode, material);
            }
        }

        // Validate combined demand for each material before creating the order.
        validateInventoryAvailability(request);

        // Initialize the new sales order with its customer, plant, and status.
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setOrderNumber(request.orderNumber());
        salesOrder.setCustomer(customer);
        salesOrder.setPlant(plant);
        salesOrder.setStatus("CREATED");
        salesOrder.setTotalAmount(BigDecimal.ZERO);

        BigDecimal orderTotal = BigDecimal.ZERO;
        List<SalesOrderItem> items = new ArrayList<>();

        // Build each order line and calculate its monetary value.
        for (CreateSalesOrderRequest.SalesOrderItemRequest itemRequest
                : request.items()) {

            Material material = materialsByCode.get(itemRequest.materialCode());
            BigDecimal quantity = itemRequest.orderedQuantity();

            // Keep unit prices at two decimal places.
            BigDecimal unitPrice = itemRequest.unitPrice()
                    .setScale(2, RoundingMode.HALF_UP);

            // Calculate the line total using the requested quantity and unit price.
            BigDecimal lineTotal = quantity.multiply(unitPrice)
                    .setScale(2, RoundingMode.HALF_UP);

            // Associate the material and calculated amounts with the order line.
            SalesOrderItem item = new SalesOrderItem();
            item.setSalesOrder(salesOrder);
            item.setMaterial(material);
            item.setOrderedQuantity(quantity);
            item.setUnitPrice(unitPrice);
            item.setLineTotal(lineTotal);

            items.add(item);
            orderTotal = orderTotal.add(lineTotal);
        }

        // Set the final order total to two decimal places.
        salesOrder.setTotalAmount(orderTotal.setScale(2, RoundingMode.HALF_UP));

        // Attach all order lines to the parent order.
        salesOrder.getItems().addAll(items);

        // Persist the order and its items within the transaction.
        SalesOrder savedOrder = salesOrderRepository.save(salesOrder);

        // Convert the saved entity into the API response DTO.
        return toResponse(savedOrder);
    }

    // Sum a material's inventory across all storage locations in the selected plant.
    private BigDecimal getAvailableQuantity(String materialCode, String plantCode) {

        List<StorageLocation> storageLocations =
                storageLocationRepository.findByPlant_PlantCode(plantCode);

        BigDecimal availableQuantity = BigDecimal.ZERO;

        // Add stock from each storage location that has an inventory record.
        for (StorageLocation location : storageLocations) {

            var inventory = inventoryRepository
                    .findByMaterial_MaterialCodeAndStorageLocation_Id(
                            materialCode, location.getId());

            if (inventory.isPresent()) {
                availableQuantity = availableQuantity.add(
                        inventory.get().getQuantity());
            }
        }

        return availableQuantity;
    }

    // Aggregate repeated material lines and reject orders exceeding available stock.
    private void validateInventoryAvailability(CreateSalesOrderRequest request) {

        Map<String, BigDecimal> requestedQuantities = new LinkedHashMap<>();

        // Combine quantities when the same material appears on multiple lines.
        for (CreateSalesOrderRequest.SalesOrderItemRequest item : request.items()) {
            requestedQuantities.merge(
                    item.materialCode(),
                    item.orderedQuantity(),
                    BigDecimal::add);
        }

        // Compare each material's total requested quantity against plant stock.
        for (Map.Entry<String, BigDecimal> entry : requestedQuantities.entrySet()) {

            String materialCode = entry.getKey();
            BigDecimal totalRequested = entry.getValue();

            BigDecimal availableQuantity = getAvailableQuantity(
                    materialCode, request.plantCode());

            // Reject the order if the requested quantity exceeds available stock.
            if (availableQuantity.compareTo(totalRequested) < 0) {
                throw new InsufficientInventoryException(
                        "Insufficient inventory for material: "
                                + materialCode
                                + ". Available: " + availableQuantity
                                + ", requested: " + totalRequested);
            }
        }
    }

    // Map a sales order entity and its items to the response DTO.
    private SalesOrderResponse toResponse(SalesOrder order) {

        List<SalesOrderResponse.SalesOrderItemResponse> itemResponses =
                order.getItems().stream()
                        .map(item -> new SalesOrderResponse.SalesOrderItemResponse(
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

    // Retrieve one sales order by its order number.
    @Transactional
    public SalesOrderResponse getSalesOrderByOrderNumber(String orderNumber) {

        SalesOrder order = salesOrderRepository
                .findByOrderNumber(orderNumber)
                .orElseThrow(() -> new SalesOrderNotFoundException(
                        "Sales order not found: " + orderNumber));

        return toResponse(order);
    }

    // Retrieve all sales orders and convert them to response DTOs.
    @Transactional
    public List<SalesOrderResponse> getAllSalesOrders() {

        return salesOrderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Confirm an order only when its current status is CREATED.
    @Transactional
    public SalesOrderResponse updateSalesOrderStatus(
            String orderNumber, String requestedStatus) {

        // Find the order or return a not-found error.
        SalesOrder order = salesOrderRepository
                .findByOrderNumber(orderNumber)
                .orElseThrow(() -> new SalesOrderNotFoundException(
                        "Sales order not found: " + orderNumber));

        // Prevent confirmation if the order is no longer in CREATED status.
        if (!"CREATED".equals(order.getStatus())) {
            throw new InvalidSalesOrderStatusException(
                    "Only sales orders with CREATED status can be confirmed. "
                            + "Current status: " + order.getStatus());
        }

        // Accept only CONFIRMED as a valid transition from CREATED.
        if (!"CONFIRMED".equalsIgnoreCase(requestedStatus)) {
            throw new InvalidSalesOrderStatusException(
                    "Only the CONFIRMED status is allowed from CREATED.");
        }

        // Update and persist the order's confirmed status.
        order.setStatus("CONFIRMED");
        SalesOrder savedOrder = salesOrderRepository.save(order);

        return toResponse(savedOrder);
    }

    // Cancel an order only when its current status is CREATED or CONFIRMED.
    @Transactional
    public SalesOrderResponse cancelSalesOrder(String orderNumber) {

        // Find the order or return a not-found error.
        SalesOrder order = salesOrderRepository
                .findByOrderNumber(orderNumber)
                .orElseThrow(() -> new SalesOrderNotFoundException(
                        "Sales order not found: " + orderNumber));

        // Reject cancellation when the order is already cancelled or otherwise ineligible.
        if (!"CREATED".equals(order.getStatus())
                && !"CONFIRMED".equals(order.getStatus())) {
            throw new InvalidSalesOrderStatusException(
                    "Only sales orders with CREATED or CONFIRMED status "
                            + "can be cancelled. Current status: "
                            + order.getStatus());
        }

        // Update and persist the cancelled status without changing inventory.
        order.setStatus("CANCELLED");
        SalesOrder savedOrder = salesOrderRepository.save(order);

        return toResponse(savedOrder);
    }
}
