package com.enterprise.erp.repository;

import com.enterprise.erp.entity.BusinessPartner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessPartnerRepository
       extends JpaRepository<BusinessPartner, Long> {

    //duplicate partner-code check
    boolean existsByPartnerCode(String partnerCode);

    Optional<BusinessPartner> findByPartnerCode(String partnerCode);
}
