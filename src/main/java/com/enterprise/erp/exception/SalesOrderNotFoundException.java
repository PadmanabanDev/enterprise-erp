package com.enterprise.erp.exception;

public class SalesOrderNotFoundException extends RuntimeException {
    public SalesOrderNotFoundException(String message) {
        super(message);
    }
}
