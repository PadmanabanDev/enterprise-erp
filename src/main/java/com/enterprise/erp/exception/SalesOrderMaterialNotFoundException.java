package com.enterprise.erp.exception;

public class SalesOrderMaterialNotFoundException extends RuntimeException {
    public SalesOrderMaterialNotFoundException(String message) {
        super(message);
    }
}
