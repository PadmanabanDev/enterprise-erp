package com.enterprise.erp.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(
            name = "inventory",
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_inventory_material_location",
                            columnNames = {"material_id", "storage_location_id"}
                    )
            }
    )
public class Inventory {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        //many inventory records refers the same material
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "material_id", nullable = false)
        private Material material;

        //Many Inventory records can reference the same storage location.
        //join column tell which fk col to use
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "storage_location_id", nullable = false)
        private StorageLocation storageLocation;

        @Column(nullable = false, precision = 15, scale = 3)
        private BigDecimal quantity = BigDecimal.ZERO;

        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at", nullable = false)
        private LocalDateTime updatedAt;

        @PrePersist
        protected void onCreate() {
            LocalDateTime now = LocalDateTime.now();
            createdAt = now;
            updatedAt = now;
        }

        @PreUpdate
        protected void onUpdate() {
            updatedAt = LocalDateTime.now();
        }
}
