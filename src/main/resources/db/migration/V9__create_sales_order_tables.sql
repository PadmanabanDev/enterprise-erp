
CREATE TABLE sales_order (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_number VARCHAR(30) NOT NULL UNIQUE,
                             customer_id BIGINT NOT NULL,
                             plant_id BIGINT NOT NULL,
                             status VARCHAR(30) NOT NULL DEFAULT 'CREATED',
                             order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             total_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT fk_sales_order_customer
                                 FOREIGN KEY (customer_id)
                                     REFERENCES business_partner(id),

                             CONSTRAINT fk_sales_order_plant
                                 FOREIGN KEY (plant_id)
                                     REFERENCES plant(id),

                             CONSTRAINT chk_sales_order_total
                                 CHECK (total_amount >= 0)
);

CREATE TABLE sales_order_item (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  sales_order_id BIGINT NOT NULL,
                                  material_id BIGINT NOT NULL,
                                  ordered_quantity DECIMAL(15, 3) NOT NULL,
                                  unit_price DECIMAL(15, 2) NOT NULL,
                                  line_total DECIMAL(15, 2) NOT NULL,

                                  CONSTRAINT fk_sales_order_item_order
                                      FOREIGN KEY (sales_order_id)
                                          REFERENCES sales_order(id),

                                  CONSTRAINT fk_sales_order_item_material
                                      FOREIGN KEY (material_id)
                                          REFERENCES material(id),

                                  CONSTRAINT chk_sales_order_item_quantity
                                      CHECK (ordered_quantity > 0),

                                  CONSTRAINT chk_sales_order_item_price
                                      CHECK (unit_price >= 0),

                                  CONSTRAINT chk_sales_order_item_line_total
                                      CHECK (line_total >= 0)
);
