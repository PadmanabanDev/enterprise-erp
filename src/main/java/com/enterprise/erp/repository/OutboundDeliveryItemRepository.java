
package com.enterprise.erp.repository;

import com.enterprise.erp.entity.OutboundDeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboundDeliveryItemRepository
        extends JpaRepository<OutboundDeliveryItem, Long> {

    // Retrieve all delivery item lines belonging to a delivery.
    List<OutboundDeliveryItem> findByDeliveryId(Long deliveryId);

    // Retrieve delivery lines for a specific sales order item across deliveries.
    List<OutboundDeliveryItem> findBySalesOrderItemId(Long salesOrderItemId);
}
