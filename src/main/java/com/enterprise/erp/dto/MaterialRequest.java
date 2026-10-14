package com.enterprise.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialRequest {

    @NotBlank
    @Size(max=30)
    private  String materialCode;

    @NotBlank
    @Size(max=150)
    private String description;

    @NotBlank
    @Size(max=30)
    private String materialType;

    @NotBlank
    @Size(max=10)
    private String baseUnit;
}
