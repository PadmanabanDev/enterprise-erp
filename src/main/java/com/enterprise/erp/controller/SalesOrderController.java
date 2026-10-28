
package com.enterprise.erp.controller;

import com.enterprise.erp.dto.CreateSalesOrderRequest;
import com.enterprise.erp.dto.SalesOrderResponse;
import com.enterprise.erp.dto.UpdateSalesOrderStatusRequest;
import com.enterprise.erp.service.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    public ResponseEntity<SalesOrderResponse> createSalesOrder(
            @Valid @RequestBody CreateSalesOrderRequest request) {

        SalesOrderResponse response =
                salesOrderService.createSalesOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<SalesOrderResponse> getSalesOrder(
            @PathVariable String orderNumber) {

        SalesOrderResponse response =
                salesOrderService.getSalesOrderByOrderNumber(orderNumber);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SalesOrderResponse>> getAllSalesOrders() {

        List<SalesOrderResponse> response =
                salesOrderService.getAllSalesOrders();

        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{orderNumber}/status")
    public ResponseEntity<SalesOrderResponse> updateSalesOrderStatus(
            @PathVariable String orderNumber,
            @Valid @RequestBody UpdateSalesOrderStatusRequest request) {

        SalesOrderResponse response =
                salesOrderService.updateSalesOrderStatus(
                        orderNumber,
                        request.status());

        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{orderNumber}/cancel")
    public ResponseEntity<SalesOrderResponse> cancelSalesOrder(
            @PathVariable String orderNumber) {

        SalesOrderResponse response =
                salesOrderService.cancelSalesOrder(orderNumber);

        return ResponseEntity.ok(response);
    }


}
