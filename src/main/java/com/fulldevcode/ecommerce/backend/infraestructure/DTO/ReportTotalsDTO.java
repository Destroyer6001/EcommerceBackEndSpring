package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportTotalsDTO {

    private String name;

    private Long total;

    public ReportTotalsDTO (String name, Long total)
    {
        this.name = name;
        this.total = total;
    }
}
