CREATE TABLE business_partner (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  partner_code VARCHAR(20) NOT NULL UNIQUE,
                                  partner_type VARCHAR(20) NOT NULL,
                                  name VARCHAR(100) NOT NULL,
                                  email VARCHAR(150),
                                  phone VARCHAR(20),
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);