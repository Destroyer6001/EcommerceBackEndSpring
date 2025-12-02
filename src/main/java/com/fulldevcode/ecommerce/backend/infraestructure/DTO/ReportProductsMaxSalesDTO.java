package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportProductsMaxSalesDTO {

    private String name;

    private Long quantity;

    public ReportProductsMaxSalesDTO(String name, Long quantity)
    {
        this.name = name;
        this.quantity = quantity;
    }
}
