
package com.enterprise.erp.exception;

public class GoodsReceiptPlantMismatchException extends RuntimeException {

    public GoodsReceiptPlantMismatchException() {
        super("Plant does not match the purchase order.");
    }
}
