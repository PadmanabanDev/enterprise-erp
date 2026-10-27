
package com.enterprise.erp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class GoodsReceiptRequest {

    @NotBlank(message = "Receipt number is required")
    @Size(max = 30, message = "Receipt number must not exceed 30 characters")
    private String receiptNumber;

    @NotBlank(message = "Purchase order number is required")
    private String purchaseOrderNumber;

    @NotBlank(message = "Plant code is required")
    private String plantCode;

    @NotBlank(message = "Storage location code is required")
    private String storageLocationCode;

    @NotEmpty(message = "At least one receipt item is required")
    @Valid
    private List<GoodsReceiptItemRequest> items;

    public GoodsReceiptRequest() {
    }


}
