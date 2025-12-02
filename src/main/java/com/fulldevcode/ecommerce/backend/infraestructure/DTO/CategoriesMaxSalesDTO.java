package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoriesMaxSalesDTO {

    private String name;

    private Long numberProductsCategories;

    public CategoriesMaxSalesDTO(String name, Long number)
    {
        this.name = name;
        this.numberProductsCategories = number;
    }
}
