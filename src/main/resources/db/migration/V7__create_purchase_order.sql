
CREATE TABLE purchase_order (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                order_number VARCHAR(30) NOT NULL UNIQUE,
                                supplier_id BIGINT NOT NULL,
                                plant_id BIGINT NOT NULL,
                                status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
                                order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_purchase_order_supplier
                                    FOREIGN KEY (supplier_id)
                                        REFERENCES business_partner(id),

                                CONSTRAINT fk_purchase_order_plant
                                    FOREIGN KEY (plant_id)
                                        REFERENCES plant(id)
);

CREATE TABLE purchase_order_item (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     purchase_order_id BIGINT NOT NULL,
                                     material_id BIGINT NOT NULL,
                                     ordered_quantity DECIMAL(15, 3) NOT NULL,
                                     unit_price DECIMAL(15, 2) NOT NULL,

                                     CONSTRAINT fk_po_item_order
                                         FOREIGN KEY (purchase_order_id)
                                             REFERENCES purchase_order(id),

                                     CONSTRAINT fk_po_item_material
                                         FOREIGN KEY (material_id)
                                             REFERENCES material(id),

                                     CONSTRAINT chk_po_item_quantity
                                         CHECK (ordered_quantity > 0),

                                     CONSTRAINT chk_po_item_price
                                         CHECK (unit_price >= 0)
);
