package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProductDTO {
    private Integer id;
    private String name;
    private String description;
    private Integer stock;
    private Integer salePrice;
    private Integer sale;
    private Integer CategoryId;
    private String imagen;
    private LocalDateTime createdDate;
}
