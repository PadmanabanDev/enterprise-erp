package com.enterprise.erp.repository;

import com.enterprise.erp.entity.BusinessPartner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessPartnerRepository
       extends JpaRepository<BusinessPartner, Long> {

    //duplicate partner-code check
    boolean existsByPartnerCode(String partnerCode);
}
