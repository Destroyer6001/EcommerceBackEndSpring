package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ShipmentsDetails {

    private Integer id;

    private String state;

    private LocalDateTime date;

    private LocalDateTime deliveryDate;

    public ShipmentsDetails (Integer id, String state, LocalDateTime date, LocalDateTime deliveryDate)
    {
        this.id = id;
        this.state = state;
        this.deliveryDate = deliveryDate;
        this.date = date;
    }
}
