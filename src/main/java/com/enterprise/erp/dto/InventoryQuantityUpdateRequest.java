
package com.enterprise.erp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class InventoryQuantityUpdateRequest {

    @NotNull(message = "Quantity is required")
    @DecimalMin(
            value = "0.000",
            message = "Quantity cannot be negative"
    )
    private BigDecimal quantity;

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
