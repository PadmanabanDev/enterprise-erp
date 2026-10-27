
package com.enterprise.erp.exception;

public class DuplicateGoodsReceiptItemException extends RuntimeException {

    public DuplicateGoodsReceiptItemException(Long purchaseOrderItemId) {
        super(
                "Purchase order item cannot appear more than once "
                        + "in the same goods receipt. Item ID: "
                        + purchaseOrderItemId
        );
    }
}
