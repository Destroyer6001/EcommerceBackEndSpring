package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;

@Data
public class ChangeStateDTO {

    private int orderId;

    private int state;

}
