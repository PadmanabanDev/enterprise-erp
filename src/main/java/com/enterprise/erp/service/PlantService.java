package com.enterprise.erp.service;

import com.enterprise.erp.dto.PlantRequest;
import com.enterprise.erp.dto.PlantResponse;
import com.enterprise.erp.entity.Plant;
import com.enterprise.erp.exception.DuplicatePlantException;
import com.enterprise.erp.exception.PlantNotFoundException;
import com.enterprise.erp.mapper.PlantMapper;
import com.enterprise.erp.repository.PlantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantService {
    private final PlantRepository plantRepository;
    private final PlantMapper plantMapper;

    public PlantService(PlantRepository plantRepository, PlantMapper plantMapper){
        this.plantRepository = plantRepository;
        this.plantMapper= plantMapper;
    }

    //Create Plant record
    public PlantResponse createPlant(PlantRequest request){

        //Duplicate Plant Record Check
        if(plantRepository.existsByPlantCode(request.getPlantCode())){
            throw new DuplicatePlantException(
                    "Plant already exists with Plant code: "+ request.getPlantCode());
        }

        //Map and save
        Plant plant = plantMapper.toEntity(request);
        Plant savedPlant = plantRepository.save(plant);

        return plantMapper.toResponse(savedPlant);
    }

    //Get All Plant Records
    public List<PlantResponse> getAllPlants(){
       return plantRepository.findAll()
               .stream()
               .map(plantMapper::toResponse)
               .toList();

    }

    //get Plant by plantCode
    public PlantResponse getPlantByCode(String plantCode){
        Plant plant = plantRepository.findByPlantCode(plantCode)
                .orElseThrow(()->
                        new PlantNotFoundException(
                                "Plant code not found: "+ plantCode));
        return plantMapper.toResponse(plant);
    }
}
