
package com.enterprise.erp.exception;

public class PurchaseOrderItemNotFoundException extends RuntimeException {

    public PurchaseOrderItemNotFoundException(Long itemId) {
        super("Purchase order item not found: " + itemId);
    }
}
