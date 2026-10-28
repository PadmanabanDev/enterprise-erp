package com.enterprise.erp.repository;

import com.enterprise.erp.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalesOrderRepository  extends JpaRepository<SalesOrder, Long> {

    boolean existsByOrderNumber(String orderNumber);

    Optional<SalesOrder> findByOrderNumber(String orderNumber);
}
