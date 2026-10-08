package com.enterprise.erp.service;

import com.enterprise.erp.dto.MaterialResponse;
import com.enterprise.erp.mapper.MaterialMapper;
import com.enterprise.erp.repository.MaterialRepository;
import org.springframework.stereotype.Service;

@Service
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    public MaterialService (MaterialRepository materialRepository, MaterialMapper materialMapper){
        this.materialRepository= materialRepository;
        this.materialMapper = materialMapper;
    }



}
