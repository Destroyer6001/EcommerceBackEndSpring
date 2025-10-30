package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDetailsDTO {
    private Integer id;
    private String name;
    private String description;
    private Integer stock;
    private Integer sale;
    private Integer salePrice;
    private String categoryName;
    private String image;

    public ProductDetailsDTO(Integer id, String name, String description, Integer stock, Integer sale, Integer salePrice, String categoryName, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.sale = sale;
        this.salePrice = salePrice;
        this.categoryName = categoryName;
        this.image = image;
    }

}
