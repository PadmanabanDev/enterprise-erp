package com.enterprise.erp.mapper;

import com.enterprise.erp.dto.BusinessPartnerRequest;
import com.enterprise.erp.dto.BusinessPartnerResponse;
import com.enterprise.erp.entity.BusinessPartner;

public class BusinessPartnerMapper {

    //BusinessPartnerRequest DTO -> BusinessPartner Entity
    public BusinessPartner toEntity(BusinessPartnerRequest request){

        BusinessPartner entity = new BusinessPartner();

        entity.setPartnerCode(request.getPartnerCode());
        entity.setPartnerType(request.getPartnerType());
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());


        return entity;
    }

    //BusinessPartner Entity  --> BusinessPartnerResponse DTO
    public BusinessPartnerResponse toResponse(BusinessPartner entity){

        BusinessPartnerResponse response = new BusinessPartnerResponse();

        response.setId(entity.getId());
        response.setPartnerCode(entity.getPartnerCode());
        response.setPartnerType(entity.getPartnerType());
        response.setName(entity.getName());
        response.setEmail(entity.getEmail());
        response.setPhone(entity.getPhone());
        response.setCreatedAt(entity.getCreatedAt());

        return response;
    }
}
