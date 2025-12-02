package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportProductTotalSalesDTO {

    public String name;

    private Long totalSales;

    public ReportProductTotalSalesDTO(String name, Long total)
    {
        this.name = name;
        this.totalSales = total;
    }
}
