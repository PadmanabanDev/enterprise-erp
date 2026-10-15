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
public class InventoryRequest {
    @NotBlank(message = "Material code is required")
    @Size(max = 30)
    private String materialCode;

    @NotBlank(message = "Plant code is required")
    @Size(max = 10)
    private String plantCode;

    @NotBlank(message = "Storage location code is required")
    @Size(max = 10)
    private String storageLocationCode;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.000", message = "Quantity cannot be negative")
    private BigDecimal quantity;

}
