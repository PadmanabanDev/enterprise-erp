
-- Create the outbound delivery header table.
CREATE TABLE outbound_delivery (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   delivery_number VARCHAR(30) NOT NULL UNIQUE,
                                   sales_order_id BIGINT NOT NULL,
                                   status VARCHAR(30) NOT NULL DEFAULT 'CREATED',
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_outbound_delivery_sales_order
                                       FOREIGN KEY (sales_order_id)
                                           REFERENCES sales_order(id),

                                   CONSTRAINT chk_outbound_delivery_status
                                       CHECK (status IN ('CREATED', 'POSTED', 'CANCELLED'))
);

-- Create delivery item lines linked to sales order items and inventory locations.
CREATE TABLE outbound_delivery_item (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        delivery_id BIGINT NOT NULL,
                                        sales_order_item_id BIGINT NOT NULL,
                                        material_id BIGINT NOT NULL,
                                        storage_location_id BIGINT NOT NULL,
                                        delivered_quantity DECIMAL(15, 3) NOT NULL,

                                        CONSTRAINT fk_delivery_item_delivery
                                            FOREIGN KEY (delivery_id)
                                                REFERENCES outbound_delivery(id),

                                        CONSTRAINT fk_delivery_item_sales_order_item
                                            FOREIGN KEY (sales_order_item_id)
                                                REFERENCES sales_order_item(id),

                                        CONSTRAINT fk_delivery_item_material
                                            FOREIGN KEY (material_id)
                                                REFERENCES material(id),

                                        CONSTRAINT fk_delivery_item_storage_location
                                            FOREIGN KEY (storage_location_id)
                                                REFERENCES storage_location(id),

                                        CONSTRAINT chk_delivery_item_quantity
                                            CHECK (delivered_quantity > 0)
);
