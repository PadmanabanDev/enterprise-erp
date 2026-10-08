package com.enterprise.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="material")
public class Material {

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "material_code", nullable = false, unique = true, length = 30)
    private String materialCode;

    @Column(name = "description", nullable = false, length = 150)
    private String description;

    @Column(name = "material_type", nullable = false, length = 30)
    private String materialType;

    @Column(name = "base_unit", nullable = false, length = 10)
    private String baseUnit;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


}
