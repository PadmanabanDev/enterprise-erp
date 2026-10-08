package com.enterprise.erp.controller;

import com.enterprise.erp.dto.BusinessPartnerRequest;
import com.enterprise.erp.dto.BusinessPartnerResponse;
import com.enterprise.erp.service.BusinessPartnerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business-partners")
public class BusinessPartnerController {

    private final BusinessPartnerService businessPartnerService;

    public BusinessPartnerController(BusinessPartnerService businessPartnerService){
        this.businessPartnerService = businessPartnerService;
    }

    @PostMapping
    public BusinessPartnerResponse createBusinessPartner(
           @Valid @RequestBody BusinessPartnerRequest request
            ){
        return businessPartnerService.createBusinessPartner(request);
    }

    @GetMapping
    public List<BusinessPartnerResponse> getAllBusinessPartners(){
        return businessPartnerService.getAllBusinessPartners();
    }


    @GetMapping("/{partnerCode}")
    public BusinessPartnerResponse getBusinessPartnerByCode(
            @PathVariable String partnerCode) {

        return businessPartnerService.getBusinessPartnerByCode(partnerCode);
    }
}
