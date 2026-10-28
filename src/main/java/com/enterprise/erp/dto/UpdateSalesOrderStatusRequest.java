
package com.enterprise.erp.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateSalesOrderStatusRequest(

        @NotBlank(message = "Status is required")
        String status

) {}
