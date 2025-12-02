package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoriesTotalSalesDTO {

    private String name;

    private Long totalSalesCategories;

    public CategoriesTotalSalesDTO(String name, Long total)
    {
        this.name = name;
        this.totalSalesCategories = total;
    }

}
