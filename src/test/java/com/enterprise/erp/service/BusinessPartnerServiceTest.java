package com.enterprise.erp.service;

import com.enterprise.erp.entity.BusinessPartner;
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

    private BusinessPartnerService businessPartnerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        businessPartnerService =
                new BusinessPartnerService(businessPartnerRepository);
    }

    @Test
    void shouldCreateBusinessPartner() {

        // Arrange
        BusinessPartner businessPartner = new BusinessPartner();
        businessPartner.setPartnerCode("BP1001");
        businessPartner.setPartnerType("CUSTOMER");
        businessPartner.setName("ABC Industries");

        when(businessPartnerRepository.save(businessPartner))
                .thenReturn(businessPartner);

        // Act
        BusinessPartner result =
                businessPartnerService.createBusinessPartner(businessPartner);

        // Assert
        assertEquals("BP1001", result.getPartnerCode());
        assertEquals("ABC Industries", result.getName());

        verify(businessPartnerRepository).save(businessPartner);

    }

    @Test
    void shouldRejectDuplicatePartnerCode() {

        BusinessPartner businessPartner = new BusinessPartner();
        businessPartner.setPartnerCode("BP1001");
        businessPartner.setPartnerType("CUSTOMER");
        businessPartner.setName("ABC Industries");

        when(businessPartnerRepository.existsByPartnerCode("BP1001"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> businessPartnerService.createBusinessPartner(businessPartner)
        );

        verify(businessPartnerRepository)
                .existsByPartnerCode("BP1001");

        verify(businessPartnerRepository, never())
                .save(businessPartner);
    }
}