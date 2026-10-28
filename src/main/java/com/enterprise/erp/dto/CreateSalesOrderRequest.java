
package com.enterprise.erp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.util.List;

public record CreateSalesOrderRequest(

        @NotBlank(message = "Order number is required")
        String orderNumber,

        @NotBlank(message = "Customer code is required")
        String customerCode,

        @NotBlank(message = "Plant code is required")
        String plantCode,

        @NotEmpty(message = "At least one sales order item is required")
        List<@Valid SalesOrderItemRequest> items

) {

    public record SalesOrderItemRequest(

            @NotBlank(message = "Material code is required")
            String materialCode,

            @NotNull(message = "Ordered quantity is required")
            @DecimalMin(value = "0.001", message = "Ordered quantity must be greater than zero")
            @Digits(integer = 12, fraction = 3, message = "Quantity supports up to 3 decimal places")
            BigDecimal orderedQuantity,

            @NotNull(message = "Unit price is required")
            @DecimalMin(value = "0.00", message = "Unit price cannot be negative")
            @Digits(integer = 13, fraction = 2, message = "Unit price supports up to 2 decimal places")
            BigDecimal unitPrice

    ) {}
}
