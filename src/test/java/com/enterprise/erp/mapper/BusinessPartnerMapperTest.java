package com.enterprise.erp.mapper;

import com.enterprise.erp.dto.BusinessPartnerRequest;
import com.enterprise.erp.dto.BusinessPartnerResponse;
import com.enterprise.erp.entity.BusinessPartner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BusinessPartnerMapperTest {
    @Test
    void shouldMapRequestToEntity() {

        BusinessPartnerRequest request = new BusinessPartnerRequest();

        request.setPartnerCode("BP1001");
        request.setPartnerType("CUSTOMER");
        request.setName("ABC Industries");
        request.setEmail("abc@example.com");
        request.setPhone("9876543210");

        BusinessPartnerMapper mapper = new BusinessPartnerMapper();

        BusinessPartner result = mapper.toEntity(request);

        assertEquals("BP1001", result.getPartnerCode());
        assertEquals("CUSTOMER", result.getPartnerType());
        assertEquals("ABC Industries", result.getName());
        assertEquals("abc@example.com", result.getEmail());
        assertEquals("9876543210", result.getPhone());
    }

    @Test
    void shouldMapEntityToResponse() {

        BusinessPartner entity = new BusinessPartner();

        entity.setId(1L);
        entity.setPartnerCode("BP1001");
        entity.setPartnerType("CUSTOMER");
        entity.setName("ABC Industries");
        entity.setEmail("abc@example.com");
        entity.setPhone("9876543210");

        BusinessPartnerMapper mapper = new BusinessPartnerMapper();

        BusinessPartnerResponse result = mapper.toResponse(entity);

        assertEquals(1L, result.getId());
        assertEquals("BP1001", result.getPartnerCode());
        assertEquals("CUSTOMER", result.getPartnerType());
        assertEquals("ABC Industries", result.getName());
        assertEquals("abc@example.com", result.getEmail());
        assertEquals("9876543210", result.getPhone());
    }
}
