package com.enterprise.erp.exception;

public class DuplicatePurchaseOrderException extends RuntimeException {
    public DuplicatePurchaseOrderException(String message) {
        super(message);
    }
}
