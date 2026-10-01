package com.enterprise.erp.service;

import com.enterprise.erp.dto.BusinessPartnerRequest;
import com.enterprise.erp.dto.BusinessPartnerResponse;
import com.enterprise.erp.entity.BusinessPartner;
import com.enterprise.erp.mapper.BusinessPartnerMapper;
import com.enterprise.erp.repository.BusinessPartnerRepository;
import org.springframework.stereotype.Service;


@Service
public class BusinessPartnerService {

    private final BusinessPartnerRepository businessPartnerRepository;
    private final BusinessPartnerMapper businessPartnerMapper;

    public BusinessPartnerService(
            BusinessPartnerRepository businessPartnerRepository,
            BusinessPartnerMapper businessPartnerMapper) {

        this.businessPartnerRepository = businessPartnerRepository;
        this.businessPartnerMapper = businessPartnerMapper;
    }

    public BusinessPartnerResponse createBusinessPartner(
            BusinessPartnerRequest request) {

        BusinessPartner businessPartner =
                businessPartnerMapper.toEntity(request);

        if (businessPartnerRepository.existsByPartnerCode(
                businessPartner.getPartnerCode())) {

            throw new IllegalArgumentException(
                    "Business partner code already exists: "
                            + businessPartner.getPartnerCode());
        }

        BusinessPartner savedBusinessPartner =
                businessPartnerRepository.save(businessPartner);

        return businessPartnerMapper.toResponse(savedBusinessPartner);
    }
}
