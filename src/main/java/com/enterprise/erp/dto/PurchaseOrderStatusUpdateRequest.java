
package com.enterprise.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PurchaseOrderStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    @Pattern(
            regexp = "APPROVED|CANCELLED",
            message = "Status must be APPROVED or CANCELLED"
    )
    private String status;

    public PurchaseOrderStatusUpdateRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
