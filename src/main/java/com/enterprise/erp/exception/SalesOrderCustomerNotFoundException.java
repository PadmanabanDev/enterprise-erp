package com.enterprise.erp.exception;

public class SalesOrderCustomerNotFoundException extends RuntimeException {
    public SalesOrderCustomerNotFoundException(String message) {
        super(message);
    }
}
