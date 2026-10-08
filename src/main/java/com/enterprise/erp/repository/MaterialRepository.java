package com.enterprise.erp.repository;

import com.enterprise.erp.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialRepository
        extends JpaRepository<Material, Long> {

    boolean existsByMaterialCode(String materialCode);

    Optional<Material> findByMaterialCode(String materialCode);
}
