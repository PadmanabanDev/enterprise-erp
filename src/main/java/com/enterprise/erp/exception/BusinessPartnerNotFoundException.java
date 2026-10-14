package com.enterprise.erp.exception;

public class BusinessPartnerNotFoundException
        extends  RuntimeException{

    public BusinessPartnerNotFoundException(String message){
        super(message);
    }
}
