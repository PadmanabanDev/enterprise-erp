package com.enterprise.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlantRequest {
    @NotBlank
    @Size(max = 10)
    private String plantCode;

    @NotBlank
    @Size(max = 100)
    private String plantName;

    @NotBlank
    @Size(max = 100)
    private String city;

    @NotBlank
    @Size(max = 100)
    private String country;
}
