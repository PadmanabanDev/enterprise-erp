package com.enterprise.erp.exception;

public class DuplicateSalesOrderException extends RuntimeException {
    public DuplicateSalesOrderException(String message) {
        super(message);
    }
}
