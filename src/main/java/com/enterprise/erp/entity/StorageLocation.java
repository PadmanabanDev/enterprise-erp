
package com.enterprise.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "storage_location",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_storage_location_plant_code",
                        columnNames = {"plant_id", "storage_location_code"}
                )
        }
)
public class StorageLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "storage_location_code", nullable = false, length = 10)
    private String storageLocationCode;

    @Column(name = "storage_location_name", nullable = false, length = 100)
    private String storageLocationName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }




}
