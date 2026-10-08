package com.enterprise.erp.service;

import com.enterprise.erp.dto.BusinessPartnerRequest;
import com.enterprise.erp.dto.BusinessPartnerResponse;
import com.enterprise.erp.entity.BusinessPartner;
import com.enterprise.erp.exception.BusinessPartnerAlreadyExistsException;
import com.enterprise.erp.exception.BusinessPartnerNotFoundException;
import com.enterprise.erp.mapper.BusinessPartnerMapper;
import com.enterprise.erp.repository.BusinessPartnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;


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

    //Create BusinessPartner
    public BusinessPartnerResponse createBusinessPartner(
            BusinessPartnerRequest request) {

        BusinessPartner businessPartner =
                businessPartnerMapper.toEntity(request);

        if (businessPartnerRepository.existsByPartnerCode(
                businessPartner.getPartnerCode())) {

            throw new BusinessPartnerAlreadyExistsException(
                    "Business partner code already exists: "
                        + businessPartner.getPartnerCode()
            );
        }

        BusinessPartner savedBusinessPartner =
                businessPartnerRepository.save(businessPartner);

        return businessPartnerMapper.toResponse(savedBusinessPartner);
    }

    //Get all Business partner
    public List<BusinessPartnerResponse> getAllBusinessPartners(){
        List<BusinessPartner> businessPartners = businessPartnerRepository.findAll();
        return  businessPartners.stream()
                .map(businessPartnerMapper::toResponse)
                .toList();
    }


    public BusinessPartnerResponse getBusinessPartnerByCode(String partnerCode){

        BusinessPartner businessPartner = businessPartnerRepository.findByPartnerCode(partnerCode)
                .orElseThrow(()->
                        new BusinessPartnerNotFoundException(
                                "Business partner not found: "
                                + partnerCode
                        ));
        return  businessPartnerMapper.toResponse(businessPartner);
    }
}
