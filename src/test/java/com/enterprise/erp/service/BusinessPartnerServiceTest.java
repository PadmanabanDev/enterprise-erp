package com.enterprise.erp.service;

import com.enterprise.erp.dto.BusinessPartnerRequest;
import com.enterprise.erp.dto.BusinessPartnerResponse;
import com.enterprise.erp.entity.BusinessPartner;
import com.enterprise.erp.mapper.BusinessPartnerMapper;
import com.enterprise.erp.repository.BusinessPartnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BusinessPartnerServiceTest {

    @Mock
    private BusinessPartnerRepository businessPartnerRepository;

    @Mock
    private BusinessPartnerMapper businessPartnerMapper;

    private BusinessPartnerService businessPartnerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

//        businessPartnerService =
//                new BusinessPartnerService(businessPartnerRepository);

        businessPartnerService =
                new BusinessPartnerService(
                        businessPartnerRepository,
                        businessPartnerMapper
                );
    }

    @Test
    void shouldCreateBusinessPartner() {

        // Arrange
        BusinessPartnerRequest request = new BusinessPartnerRequest();

        request.setPartnerCode("BP1001");
        request.setPartnerType("CUSTOMER");
        request.setName("ABC Industries");
        request.setEmail("abc@example.com");
        request.setPhone("9876543210");

        BusinessPartner businessPartner = new BusinessPartner();

        businessPartner.setPartnerCode("BP1001");
        businessPartner.setPartnerType("CUSTOMER");
        businessPartner.setName("ABC Industries");
        businessPartner.setEmail("abc@example.com");
        businessPartner.setPhone("9876543210");

        BusinessPartnerResponse response = new BusinessPartnerResponse();

        response.setId(1L);
        response.setPartnerCode("BP1001");
        response.setPartnerType("CUSTOMER");
        response.setName("ABC Industries");
        response.setEmail("abc@example.com");
        response.setPhone("9876543210");

        when(businessPartnerMapper.toEntity(request))
                .thenReturn(businessPartner);

        when(businessPartnerRepository.existsByPartnerCode("BP1001"))
                .thenReturn(false);

        when(businessPartnerRepository.save(businessPartner))
                .thenReturn(businessPartner);

        when(businessPartnerMapper.toResponse(businessPartner))
                .thenReturn(response);

        // Act
        BusinessPartnerResponse result =
                businessPartnerService.createBusinessPartner(request);


        // Assert
        assertEquals(1L, result.getId());
        assertEquals("BP1001", result.getPartnerCode());
        assertEquals("ABC Industries", result.getName());

        verify(businessPartnerMapper).toEntity(request);
        verify(businessPartnerRepository)
                .existsByPartnerCode("BP1001");
        verify(businessPartnerRepository).save(businessPartner);
        verify(businessPartnerMapper).toResponse(businessPartner);
    }
    @Test
    void shouldRejectDuplicatePartnerCode() {

        // Arrange
        BusinessPartnerRequest request = new BusinessPartnerRequest();

        request.setPartnerCode("BP1001");
        request.setPartnerType("CUSTOMER");
        request.setName("ABC Industries");

        BusinessPartner businessPartner = new BusinessPartner();

        businessPartner.setPartnerCode("BP1001");
        businessPartner.setPartnerType("CUSTOMER");
        businessPartner.setName("ABC Industries");

        when(businessPartnerMapper.toEntity(request))
                .thenReturn(businessPartner);

        when(businessPartnerRepository.existsByPartnerCode("BP1001"))
                .thenReturn(true);

        // Act + Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> businessPartnerService.createBusinessPartner(request)
        );

        verify(businessPartnerMapper).toEntity(request);

        verify(businessPartnerRepository)
                .existsByPartnerCode("BP1001");

        verify(businessPartnerRepository, never())
                .save(businessPartner);
    }
}