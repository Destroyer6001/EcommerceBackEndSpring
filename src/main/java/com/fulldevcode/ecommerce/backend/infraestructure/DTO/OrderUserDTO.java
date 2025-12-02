package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderUserDTO {

    private int id;

    private String username;

    private LocalDateTime orderDate;

    private String state;

    private Integer total;

    private List<OrderProductsUserDTO> detailsUser;

}
