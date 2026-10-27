
package com.enterprise.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class GoodsReceiptResponse {

    private Long id;
    private String receiptNumber;
    private String purchaseOrderNumber;
    private String plantCode;
    private String storageLocationCode;
    private LocalDateTime receiptDate;
    private List<GoodsReceiptItemResponse> items;

    public GoodsReceiptResponse() {
    }


}
