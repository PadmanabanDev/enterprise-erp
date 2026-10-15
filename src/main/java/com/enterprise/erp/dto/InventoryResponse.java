package com.enterprise.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
public class InventoryResponse {

    private Long id;
    private String materialCode;
    private String materialDescription;
    private String plantCode;
    private String storageLocationCode;
    private String storageLocationName;
    private BigDecimal quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
