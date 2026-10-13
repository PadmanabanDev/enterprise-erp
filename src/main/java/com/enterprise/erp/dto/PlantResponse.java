package com.enterprise.erp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class PlantResponse {

    private Long id;
    private String plantCode;
    private String plantName;
    private String city;
    private String country;
    private LocalDateTime createdAt;
}
