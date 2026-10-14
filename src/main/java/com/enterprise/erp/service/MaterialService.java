package com.enterprise.erp.service;

import com.enterprise.erp.dto.MaterialRequest;
import com.enterprise.erp.dto.MaterialResponse;
import com.enterprise.erp.dto.MaterialUpdateRequest;
import com.enterprise.erp.entity.Material;
import com.enterprise.erp.exception.DuplicateMaterialException;
import com.enterprise.erp.exception.MaterialNotFoundException;
import com.enterprise.erp.mapper.MaterialMapper;
import com.enterprise.erp.repository.MaterialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    public MaterialService (MaterialRepository materialRepository, MaterialMapper materialMapper){
        this.materialRepository= materialRepository;
        this.materialMapper = materialMapper;
    }

   public MaterialResponse createMaterial(MaterialRequest request){

        //Duplicate Material check
       if(materialRepository.existsByMaterialCode(request.getMaterialCode())){
           throw  new DuplicateMaterialException(
                   "Material already exists with Material code: "+ request.getMaterialCode()
           );
       }

       //map and save
       Material material = materialMapper.toEntity(request);
       Material savedMaterial = materialRepository.save(material);

        return materialMapper.toResponse(savedMaterial);
   }

   //Get All Material
   public List<MaterialResponse> getAllMaterials(){
        List<Material> materials = materialRepository.findAll();
        return materials.stream()
                .map(materialMapper::toResponse)
                .toList();
   }

   //Get Material by MaterialCode
    public MaterialResponse getMaterialByMaterialCode(String materialCode){
        Material material = materialRepository.findByMaterialCode(materialCode)
                .orElseThrow(()->
                       new MaterialNotFoundException(
                               "Material code not found: "
                               +materialCode
                       ));
        return  materialMapper.toResponse(material);
    }

    //Update Material Record
    public MaterialResponse updateMaterial(
            String materialCode, MaterialUpdateRequest request
    ){
        Material material = materialRepository.findByMaterialCode(materialCode)
                .orElseThrow(()-> new MaterialNotFoundException(
                        "Material not Found: "+ materialCode
                ));

        material.setDescription(request.getDescription());
        material.setMaterialType(request.getMaterialType());
        material.setBaseUnit(request.getBaseUnit());

        Material updatedMaterial  = materialRepository.save(material);

        return materialMapper.toResponse(updatedMaterial );
    }

    //Delete material
    public void deleteMaterial(String materialCode) {

        Material material = materialRepository
                .findByMaterialCode(materialCode)
                .orElseThrow(() -> new MaterialNotFoundException(
                        "Material code not found: " + materialCode
                ));

        materialRepository.delete(material);
    }


}
