package com.enterprise.erp.exception;

public class MaterialNotFoundException extends RuntimeException {

    public MaterialNotFoundException(String message){
        super(message);
    }
}
