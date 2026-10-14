package com.enterprise.erp.repository;

import com.enterprise.erp.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface PlantRepository extends
        JpaRepository<Plant, Long> {
    boolean existsByPlantCode(String plantCode);

    Optional<Plant> findByPlantCode(String plantCode);
}
