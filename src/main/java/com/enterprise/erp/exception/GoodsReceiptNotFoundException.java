
package com.enterprise.erp.exception;

public class GoodsReceiptNotFoundException extends RuntimeException {

    public GoodsReceiptNotFoundException(String receiptNumber) {
        super("Goods receipt not found: " + receiptNumber);
    }
}
