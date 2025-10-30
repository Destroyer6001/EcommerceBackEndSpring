package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderDetailsDTO {

    private int id;

    private String username;

    private LocalDateTime orderDate;

    private Integer total;

    private String address;

    public OrderDetailsDTO(Integer id, String username, LocalDateTime orderDate, Integer total, String address)
    {
        this.id = id;
        this.username = username;
        this.orderDate = orderDate;
        this.total = total;
        this.address = address;
    }
}
