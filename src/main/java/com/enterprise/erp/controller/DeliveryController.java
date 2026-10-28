
package com.enterprise.erp.controller;

import com.enterprise.erp.dto.CreateDeliveryRequest;
import com.enterprise.erp.dto.DeliveryResponse;
import com.enterprise.erp.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // Create a delivery for a confirmed sales order.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryResponse createDelivery(
            @Valid @RequestBody CreateDeliveryRequest request) {
        return deliveryService.createDelivery(request);
    }

    // Retrieve a delivery by its delivery number.
    @GetMapping("/{deliveryNumber}")
    public DeliveryResponse getDeliveryByNumber(
            @PathVariable String deliveryNumber) {
        return deliveryService.getDeliveryByNumber(deliveryNumber);
    }

    // Retrieve all delivery records.
    @GetMapping
    public List<DeliveryResponse> getAllDeliveries() {
        return deliveryService.getAllDeliveries();
    }
}
