package com.enterprise.erp.controller;


import com.enterprise.erp.dto.MaterialRequest;
import com.enterprise.erp.dto.MaterialResponse;
import com.enterprise.erp.service.MaterialService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService){
        this.materialService = materialService;
    }

    @PostMapping
    public MaterialResponse createMaterial(
            @Valid @RequestBody MaterialRequest request
            ){
        return materialService.createMaterial(request);
    }

    @GetMapping
    public List<MaterialResponse> getAllMaterials(){
        return materialService.getAllMaterials();
    }

    @GetMapping("/{materialCode}")
    public MaterialResponse getMaterialByMaterialCode(
            @PathVariable String materialCode){
        return materialService.getMaterialByMaterialCode(materialCode);
    }
}
