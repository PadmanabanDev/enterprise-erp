package com.enterprise.erp.controller;

import com.enterprise.erp.dto.PlantRequest;
import com.enterprise.erp.dto.PlantResponse;
import com.enterprise.erp.service.PlantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final PlantService plantService;

    public PlantController(PlantService plantService){
        this.plantService= plantService;
    }

    @PostMapping
    public PlantResponse createPlant(
            @Valid @RequestBody PlantRequest request
            ){
        return  plantService.createPlant(request);
    }

    @GetMapping
    public List<PlantResponse> getAllPlants(){
        return plantService.getAllPlants();
    }

    @GetMapping("/{plantCode}")
    public PlantResponse getPlantByCode(
            @PathVariable String plantCode
    ){
        return plantService.getPlantByCode(plantCode);
    }

}
