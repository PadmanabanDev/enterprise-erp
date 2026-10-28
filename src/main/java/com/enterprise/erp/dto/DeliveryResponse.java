
package com.enterprise.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DeliveryResponse(

        // Return the database ID of the delivery.
        Long id,

        // Return the unique delivery number.
        String deliveryNumber,

        // Return the associated sales order number.
        String salesOrderNumber,

        // Return the current delivery status.
        String status,

        // Return when the delivery was created.
        LocalDateTime createdAt,

        // Return when the delivery was last updated.
        LocalDateTime updatedAt,

        // Return all item lines belonging to the delivery.
        List<DeliveryItemResponse> items

) {

    public record DeliveryItemResponse(

            // Return the delivery item ID.
            Long id,

            // Return the original sales order item ID.
            Long salesOrderItemId,

            // Return the material code.
            String materialCode,

            // Return the material description.
            String materialDescription,

            // Return the source storage location code.
            String storageLocationCode,

            // Return the delivered quantity.
            BigDecimal deliveredQuantity

    ) {
    }
}
