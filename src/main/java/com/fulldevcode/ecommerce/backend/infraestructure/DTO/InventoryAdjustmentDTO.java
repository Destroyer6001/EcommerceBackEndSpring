package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;

@Data
public class InventoryAdjustmentDTO {

    private Integer id;

    private Integer stock;

    private Integer purchasePrice;

    private Integer productId;
}
