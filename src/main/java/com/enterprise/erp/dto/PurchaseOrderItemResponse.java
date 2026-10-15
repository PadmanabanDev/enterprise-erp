package com.enterprise.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PurchaseOrderItemResponse {

        private Long id;
        private String materialCode;
        private String materialDescription;
        private BigDecimal orderedQuantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;

    }


