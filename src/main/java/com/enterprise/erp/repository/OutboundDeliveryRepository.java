
package com.enterprise.erp.repository;

import com.enterprise.erp.entity.OutboundDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OutboundDeliveryRepository
        extends JpaRepository<OutboundDelivery, Long> {

    // Check whether a delivery number is already registered.
    boolean existsByDeliveryNumber(String deliveryNumber);

    // Find a delivery using its unique delivery number.
    Optional<OutboundDelivery> findByDeliveryNumber(String deliveryNumber);
}
