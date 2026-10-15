
package com.enterprise.erp.repository;

import com.enterprise.erp.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseOrderRepository
        extends JpaRepository<PurchaseOrder, Long> {

    boolean existsByOrderNumber(String orderNumber);

    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);
}
