package com.enterprise.erp.controller;


import com.enterprise.erp.dto.MaterialRequest;
import com.enterprise.erp.dto.MaterialResponse;
import com.enterprise.erp.dto.MaterialUpdateRequest;
import com.enterprise.erp.service.MaterialService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/{materialCode}")
    public MaterialResponse updateMaterial(
            @PathVariable String materialCode,
            @Valid @RequestBody MaterialUpdateRequest request) {

        return materialService.updateMaterial(materialCode, request);
    }


    @DeleteMapping("/{materialCode}")
    public ResponseEntity<Void> deleteMaterial(
            @PathVariable String materialCode) {

        materialService.deleteMaterial(materialCode);

        return ResponseEntity.noContent().build();
    }


}
