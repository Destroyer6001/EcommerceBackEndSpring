package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportStatesProductsDTO {

    private String name;

    private Long totalOrders;

    public ReportStatesProductsDTO(String name, Long ordersTotal)
    {
        this.name = name;
        this.totalOrders = ordersTotal;
    }

}
