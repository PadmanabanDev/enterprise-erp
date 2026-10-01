package com.enterprise.erp.entity;

import org.junit.jupiter.api.Test;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

public class BusinessPartnerTest {

    @Test
    void shouldSetCreatedAtBeforePersist() {

        BusinessPartner businessPartner = new BusinessPartner();

        businessPartner.onCreate();

        assertNotNull(businessPartner.getCreatedAt());
    }
}
