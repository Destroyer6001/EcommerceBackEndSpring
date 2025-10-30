package com.fulldevcode.ecommerce.backend.infraestructure.Interface;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.InventoryAdjustmentDetailsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.models.InventoryAdjustmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IInventoryAdjustment extends JpaRepository<InventoryAdjustmentEntity, Integer>
{
    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.InventoryAdjustmentDetailsDTO (
                in.id,
                in.stock,
                in.purchasePrice,
                po.name,
                in.adjustmentDate
            )
            FROM InventoryAdjustmentEntity in
            JOIN in.product po
            WHERE po.id = :id
            """)
    List<InventoryAdjustmentDetailsDTO> GetALlProducts (@Param("id") Integer id);
}
