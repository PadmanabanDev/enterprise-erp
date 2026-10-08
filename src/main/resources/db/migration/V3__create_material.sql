CREATE TABLE material (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          material_code VARCHAR(30) NOT NULL UNIQUE,
                          description VARCHAR(150) NOT NULL,
                          material_type VARCHAR(30) NOT NULL,
                          base_unit VARCHAR(10) NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);