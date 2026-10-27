
package com.enterprise.erp.repository;

import com.enterprise.erp.entity.GoodsReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsReceiptItemRepository
        extends JpaRepository<GoodsReceiptItem, Long> {

    List<GoodsReceiptItem> findByGoodsReceipt_Id(Long goodsReceiptId);

    List<GoodsReceiptItem> findByPurchaseOrderItem_Id(Long purchaseOrderItemId);
}
