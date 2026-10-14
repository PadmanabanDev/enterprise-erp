
CREATE TABLE plant (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       plant_code VARCHAR(10) NOT NULL UNIQUE,
                       plant_name VARCHAR(100) NOT NULL,
                       city VARCHAR(100) NOT NULL,
                       country VARCHAR(100) NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
