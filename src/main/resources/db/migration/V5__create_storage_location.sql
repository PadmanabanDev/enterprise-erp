
CREATE TABLE storage_location (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  storage_location_code VARCHAR(10) NOT NULL,
                                  storage_location_name VARCHAR(100) NOT NULL,
                                  plant_id BIGINT NOT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT uk_storage_location_plant_code
                                      UNIQUE (plant_id, storage_location_code),

                                  CONSTRAINT fk_storage_location_plant
                                      FOREIGN KEY (plant_id)
                                          REFERENCES plant(id)
);
