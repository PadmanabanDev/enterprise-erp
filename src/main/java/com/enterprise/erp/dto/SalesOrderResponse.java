
package com.enterprise.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SalesOrderResponse(
        Long id,
        String orderNumber,
        String customerCode,
        String customerName,
        String plantCode,
        String plantName,
        String status,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        List<SalesOrderItemResponse> items
) {

    public record SalesOrderItemResponse(
            Long id,
            String materialCode,
            String materialDescription,
            BigDecimal orderedQuantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal
    ) {}
}
