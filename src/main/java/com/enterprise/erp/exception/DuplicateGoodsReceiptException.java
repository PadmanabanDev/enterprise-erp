
package com.enterprise.erp.exception;

public class DuplicateGoodsReceiptException extends RuntimeException {

    public DuplicateGoodsReceiptException(String receiptNumber) {
        super("Goods receipt already exists: " + receiptNumber);
    }
}
