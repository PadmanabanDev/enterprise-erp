package com.enterprise.erp.mapper;

import com.enterprise.erp.dto.MaterialRequest;
import com.enterprise.erp.dto.MaterialResponse;
import com.enterprise.erp.entity.Material;
import org.springframework.stereotype.Component;

@Component
public class MaterialMapper {

    //DTO request to Entity
    public Material toEntity(MaterialRequest request){

        Material entity = new Material();

        entity.setMaterialCode(request.getMaterialCode());
        entity.setDescription(request.getDescription());
        entity.setMaterialType(request.getMaterialType());
        entity.setBaseUnit(request.getBaseUnit());

        return entity;
    }

    //Entity to DTO Response
    public MaterialResponse toResponse(Material entity){
        MaterialResponse response = new MaterialResponse();

        response.setId(entity.getId());
        response.setMaterialCode(entity.getMaterialCode());
        response.setDescription(entity.getDescription());
        response.setMaterialType(entity.getMaterialType());
        response.setBaseUnit(entity.getBaseUnit());
        response.setCreatedAt(entity.getCreatedAt());

        return response;
    }
}
