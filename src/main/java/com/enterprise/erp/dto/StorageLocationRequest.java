
package com.enterprise.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorageLocationRequest {

    @NotBlank
    @Size(max = 10)
    private String storageLocationCode;

    @NotBlank
    @Size(max = 100)
    private String storageLocationName;

    @NotBlank
    @Size(max = 10)
    private String plantCode;


}
