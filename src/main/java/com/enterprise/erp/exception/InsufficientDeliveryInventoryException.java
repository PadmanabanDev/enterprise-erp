package com.enterprise.erp.exception;

// Signal that the selected storage location lacks sufficient stock.

public class InsufficientDeliveryInventoryException extends RuntimeException {
    public InsufficientDeliveryInventoryException(String message) {
        super(message);
    }
}
