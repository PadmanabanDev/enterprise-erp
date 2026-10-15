
package com.enterprise.erp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PurchaseOrderRequest {

    @NotBlank(message = "Order number is required")
    @Size(max = 30)
    private String orderNumber;

    @NotBlank(message = "Supplier code is required")
    private String supplierCode;

    @NotBlank(message = "Plant code is required")
    private String plantCode;

    @NotEmpty(message = "Purchase order must contain at least one item")
    @Valid
    private List<PurchaseOrderItemRequest> items;


}
