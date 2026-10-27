package com.enterprise.erp.exception;

public class InvalidPurchaseOrderStatusException extends RuntimeException {
    public InvalidPurchaseOrderStatusException(String message) {
        super(message);
    }
}
