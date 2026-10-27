
package com.enterprise.erp.exception;

public class PurchaseOrderItemMismatchException extends RuntimeException {

    public PurchaseOrderItemMismatchException(
            Long itemId, String orderNumber) {

        super(
                "Purchase order item " + itemId
                        + " does not belong to purchase order "
                        + orderNumber
        );
    }
}
