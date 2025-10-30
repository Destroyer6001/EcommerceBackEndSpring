package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderProductsUserDTO {

    private String productName;

    private Integer stock;

    private Integer totalPrice;

    public OrderProductsUserDTO(String productName, Integer stock, Integer totalPrice)
    {
        this.productName = productName;
        this.stock = stock;
        this.totalPrice = totalPrice;
    }
}
