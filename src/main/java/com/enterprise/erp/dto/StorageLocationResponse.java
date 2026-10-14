
package com.enterprise.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StorageLocationResponse {

    private Long id;
    private String storageLocationCode;
    private String storageLocationName;
    private String plantCode;
    private String plantName;
    private LocalDateTime createdAt;


}
