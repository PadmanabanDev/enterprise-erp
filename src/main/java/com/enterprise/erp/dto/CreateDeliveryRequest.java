
package com.enterprise.erp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.util.List;

public record CreateDeliveryRequest(

        // Require a unique delivery number.
        @NotBlank(message = "Delivery number is required")
        String deliveryNumber,

        // Identify the confirmed sales order for this delivery.
        @NotBlank(message = "Sales order number is required")
        String salesOrderNumber,

        // Require at least one delivery item.
        @NotEmpty(message = "At least one delivery item is required")
        List<@Valid DeliveryItemRequest> items

) {

    public record DeliveryItemRequest(

            // Identify the sales order line being delivered.
            @NotNull(message = "Sales order item ID is required")
            Long salesOrderItemId,

            // Identify the source storage location.
            @NotBlank(message = "Storage location code is required")
            String storageLocationCode,

            // Require a positive delivery quantity with at most three decimal places.
            @NotNull(message = "Delivered quantity is required")
            @DecimalMin(value = "0.001", message = "Delivered quantity must be greater than zero")
            @Digits(integer = 12, fraction = 3, message = "Delivered quantity supports up to 3 decimal places")
            BigDecimal deliveredQuantity

    ) {
    }
}
