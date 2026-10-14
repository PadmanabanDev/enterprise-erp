package com.enterprise.erp.mapper;

import com.enterprise.erp.dto.PlantRequest;
import com.enterprise.erp.dto.PlantResponse;
import com.enterprise.erp.entity.Plant;
import org.springframework.stereotype.Component;

@Component
public class PlantMapper {

    public Plant toEntity(PlantRequest request){

        Plant plant = new Plant();

        plant.setPlantCode(request.getPlantCode());
        plant.setPlantName(request.getPlantName());
        plant.setCity(request.getCity());
        plant.setCountry(request.getCountry());

        return  plant;
    }

    public PlantResponse toResponse(Plant plant){

        PlantResponse response = new PlantResponse();

        response.setId(plant.getId());
        response.setPlantCode(plant.getPlantCode());
        response.setPlantName(plant.getPlantName());
        response.setCity(plant.getCity());
        response.setCountry(plant.getCountry());
        response.setCreatedAt(plant.getCreatedAt());

        return response;
    }
}
