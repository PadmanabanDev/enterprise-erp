
package com.enterprise.erp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PurchaseOrderItemRequest {

    @NotBlank(message = "Material code is required")
    @Size(max = 30)
    private String materialCode;

    @NotNull(message = "Ordered quantity is required")
    @DecimalMin(value = "0.001", message = "Ordered quantity must be greater than zero")
    private BigDecimal orderedQuantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", message = "Unit price cannot be negative")
    private BigDecimal unitPrice;


}
