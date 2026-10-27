
package com.enterprise.erp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GoodsReceiptItemRequest {

    @NotNull(message = "Purchase order item ID is required")
    @Positive(message = "Purchase order item ID must be positive")
    private Long purchaseOrderItemId;

    @NotNull(message = "Received quantity is required")
    @DecimalMin(
            value = "0.001",
            message = "Received quantity must be greater than zero"
    )
    private BigDecimal receivedQuantity;

    public GoodsReceiptItemRequest() {
    }


}
