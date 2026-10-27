package com.enterprise.erp.repository;

import com.enterprise.erp.entity.GoodsReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface GoodsReceiptRepository  extends JpaRepository<GoodsReceipt, Long> {


    boolean existsByReceiptNumber(String receiptNumber);

    Optional<GoodsReceipt> findByReceiptNumber(String receiptNumber);


    @Query("""
    SELECT COALESCE(SUM(item.receivedQuantity), 0)
    FROM GoodsReceiptItem item
    WHERE item.purchaseOrderItem.id = :purchaseOrderItemId
    """)
    BigDecimal sumReceivedQuantityByPurchaseOrderItemId(
            @Param("purchaseOrderItemId") Long purchaseOrderItemId
    );

    @Query("""
    SELECT COALESCE(SUM(item.receivedQuantity), 0)
    FROM GoodsReceiptItem item
    WHERE item.goodsReceipt.purchaseOrder.id = :purchaseOrderId
    """)
    BigDecimal sumReceivedQuantityByPurchaseOrderId(
            @Param("purchaseOrderId") Long purchaseOrderId
    );

}

