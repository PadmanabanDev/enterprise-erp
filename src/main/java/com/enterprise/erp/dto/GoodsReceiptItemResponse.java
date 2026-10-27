
package com.enterprise.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class GoodsReceiptItemResponse {

    private Long id;
    private Long purchaseOrderItemId;
    private String materialCode;
    private String materialDescription;
    private BigDecimal receivedQuantity;

    public GoodsReceiptItemResponse() {
    }


}
