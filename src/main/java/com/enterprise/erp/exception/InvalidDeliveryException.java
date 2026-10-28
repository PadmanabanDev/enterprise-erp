package com.enterprise.erp.exception;
// Signal that a delivery violates a business rule.
public class InvalidDeliveryException extends RuntimeException {
    public InvalidDeliveryException(String message) {
        super(message);
    }
}
