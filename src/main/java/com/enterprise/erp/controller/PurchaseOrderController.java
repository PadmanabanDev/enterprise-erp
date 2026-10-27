
package com.enterprise.erp.controller;

import com.enterprise.erp.dto.PurchaseOrderRequest;
import com.enterprise.erp.dto.PurchaseOrderResponse;
import com.enterprise.erp.dto.PurchaseOrderStatusUpdateRequest;
import com.enterprise.erp.service.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(
            PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrderResponse createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderRequest request) {
        return purchaseOrderService.createPurchaseOrder(request);
    }

    @GetMapping("/{orderNumber}")
    public PurchaseOrderResponse getPurchaseOrderByOrderNumber(
            @PathVariable String orderNumber) {

        return purchaseOrderService
                .getPurchaseOrderByOrderNumber(orderNumber);
    }


    @GetMapping
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        return purchaseOrderService.getAllPurchaseOrders();
    }


    @PatchMapping("/{orderNumber}/status")
    public PurchaseOrderResponse updatePurchaseOrderStatus(
            @PathVariable String orderNumber,
            @Valid @RequestBody PurchaseOrderStatusUpdateRequest request) {

        return purchaseOrderService.updatePurchaseOrderStatus(
                orderNumber, request);
    }

}
