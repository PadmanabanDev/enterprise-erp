package com.enterprise.erp.controller;

import com.enterprise.erp.dto.BusinessPartnerRequest;
import com.enterprise.erp.dto.BusinessPartnerResponse;
import com.enterprise.erp.service.BusinessPartnerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/business-partners")
public class BusinessPartnerController {

    private final BusinessPartnerService businessPartnerService;

    public BusinessPartnerController(BusinessPartnerService businessPartnerService){
        this.businessPartnerService = businessPartnerService;
    }

    @PostMapping
    public BusinessPartnerResponse createBusinessPartner(
            @RequestBody BusinessPartnerRequest request
            ){
        return businessPartnerService.createBusinessPartner(request);
    }
}
