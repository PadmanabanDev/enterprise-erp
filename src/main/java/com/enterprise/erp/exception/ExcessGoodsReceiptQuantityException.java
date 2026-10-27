
package com.enterprise.erp.exception;

import java.math.BigDecimal;

public class ExcessGoodsReceiptQuantityException extends RuntimeException {

    public ExcessGoodsReceiptQuantityException(
            Long purchaseOrderItemId,
            BigDecimal outstandingQuantity) {

        super(
                "Received quantity exceeds outstanding quantity for purchase order item "
                        + purchaseOrderItemId
                        + ". Outstanding quantity: "
                        + outstandingQuantity
        );
    }
}
