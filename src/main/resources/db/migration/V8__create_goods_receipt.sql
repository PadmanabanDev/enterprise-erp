
CREATE TABLE goods_receipt (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               receipt_number VARCHAR(30) NOT NULL UNIQUE,
                               purchase_order_id BIGINT NOT NULL,
                               plant_id BIGINT NOT NULL,
                               storage_location_id BIGINT NOT NULL,
                               receipt_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_goods_receipt_purchase_order
                                   FOREIGN KEY (purchase_order_id)
                                       REFERENCES purchase_order(id),

                               CONSTRAINT fk_goods_receipt_plant
                                   FOREIGN KEY (plant_id)
                                       REFERENCES plant(id),

                               CONSTRAINT fk_goods_receipt_storage_location
                                   FOREIGN KEY (storage_location_id)
                                       REFERENCES storage_location(id)
);

CREATE TABLE goods_receipt_item (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    goods_receipt_id BIGINT NOT NULL,
                                    purchase_order_item_id BIGINT NOT NULL,
                                    received_quantity DECIMAL(15, 3) NOT NULL,

                                    CONSTRAINT fk_gr_item_receipt
                                        FOREIGN KEY (goods_receipt_id)
                                            REFERENCES goods_receipt(id),

                                    CONSTRAINT fk_gr_item_po_item
                                        FOREIGN KEY (purchase_order_item_id)
                                            REFERENCES purchase_order_item(id),

                                    CONSTRAINT chk_gr_received_quantity
                                        CHECK (received_quantity > 0)
);

