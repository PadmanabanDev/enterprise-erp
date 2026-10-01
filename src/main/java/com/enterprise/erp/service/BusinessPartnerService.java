package com.enterprise.erp.service;

import com.enterprise.erp.entity.BusinessPartner;
import com.enterprise.erp.repository.BusinessPartnerRepository;
import org.springframework.stereotype.Service;

@Service
public class BusinessPartnerService {

    private final BusinessPartnerRepository businessPartnerRepository;

    public BusinessPartnerService(BusinessPartnerRepository businessPartnerRepository) {
        this.businessPartnerRepository = businessPartnerRepository;
    }

    public BusinessPartner createBusinessPartner(BusinessPartner businessPartner){

        if(businessPartnerRepository.existsByPartnerCode(
                businessPartner.getPartnerCode())){
            throw new IllegalArgumentException(
                    "Business partner code already exists:"
                    + businessPartner.getPartnerCode());

        }
        return businessPartnerRepository.save(businessPartner);
    }
}
