
package com.enterprise.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "outbound_delivery")
public class OutboundDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique identifier used to track the delivery.
    @Column(name = "delivery_number", nullable = false, unique = true, length = 30)
    private String deliveryNumber;

    // Link the delivery to its originating sales order.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    // Track whether the delivery is created, posted, or cancelled.
    @Column(name = "status", nullable = false, length = 30)
    private String status = "CREATED";

    // Record the delivery creation timestamp.
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Record the latest delivery update timestamp.
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Maintain the delivery's item lines and persist them with the header.
    @OneToMany(
            mappedBy = "delivery",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OutboundDeliveryItem> items = new ArrayList<>();

    // Initialize timestamps when a new delivery is saved.
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    // Refresh the update timestamp whenever the delivery changes.
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
