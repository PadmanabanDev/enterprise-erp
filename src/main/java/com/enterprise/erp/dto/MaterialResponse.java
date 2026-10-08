package com.enterprise.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MaterialResponse {

    private Long id;
    private String materialCode;
    private String description;
    private String materialType;
    private String baseUnit;
    private LocalDateTime createdAt;
}
