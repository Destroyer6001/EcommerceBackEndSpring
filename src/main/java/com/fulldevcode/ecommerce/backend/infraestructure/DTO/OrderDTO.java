package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OrderDTO {

    private Integer id;

    private Integer total;

    private List<OrderProductsDTO> OrderProducts;

}
