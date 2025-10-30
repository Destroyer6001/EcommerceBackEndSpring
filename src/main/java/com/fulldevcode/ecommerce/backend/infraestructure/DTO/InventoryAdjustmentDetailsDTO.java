package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class InventoryAdjustmentDetailsDTO {

    private Integer id;

    private Integer stock;

    private Integer purchasePrice;

    private String productName;

    private LocalDateTime dateAdjustment;

    public InventoryAdjustmentDetailsDTO(Integer id, Integer stock, Integer purchasePrice, String productName, LocalDateTime dateAdjustment)
    {
        this.id = id;
        this.stock = stock;
        this.purchasePrice = purchasePrice;
        this.productName = productName;
        this.dateAdjustment = dateAdjustment;
    }
}
