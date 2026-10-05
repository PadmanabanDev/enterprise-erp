package com.enterprise.erp.controller;

import com.enterprise.erp.service.BusinessPartnerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/business-partners")
public class BusinessPartnerController {

    private final BusinessPartnerService businessPartnerService;

    public BusinessPartnerController(BusinessPartnerService businessPartnerService){
        this.businessPartnerService = businessPartnerService;
    }
}
