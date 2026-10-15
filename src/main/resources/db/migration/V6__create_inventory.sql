
CREATE TABLE inventory (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,

     material_id BIGINT NOT NULL,
     storage_location_id BIGINT NOT NULL,

     quantity DECIMAL(15, 3) NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_inventory_material_location
                               UNIQUE (material_id, storage_location_id),

    CONSTRAINT fk_inventory_material
                               FOREIGN KEY (material_id)
                                   REFERENCES material(id),

    CONSTRAINT fk_inventory_storage_location
                               FOREIGN KEY (storage_location_id)
                                   REFERENCES storage_location(id),

    CONSTRAINT chk_inventory_quantity
                               CHECK (quantity >= 0)
);
