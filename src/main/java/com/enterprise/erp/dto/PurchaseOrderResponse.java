
package com.enterprise.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PurchaseOrderResponse {

    private Long id;
    private String orderNumber;
    private String supplierCode;
    private String supplierName;
    private String plantCode;
    private String plantName;
    private String status;
    private LocalDateTime orderDate;
    private List<PurchaseOrderItemResponse> items;


}
