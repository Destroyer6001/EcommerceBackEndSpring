package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderProductsDTO {

    private Integer productId;

    private Integer stock;

    private Integer salePrice;
}
