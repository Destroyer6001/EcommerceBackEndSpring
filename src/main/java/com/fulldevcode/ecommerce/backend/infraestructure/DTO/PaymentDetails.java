package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class PaymentDetails {

    private Integer id;

    private Long payValue;

    private Boolean state;

    public PaymentDetails (Integer id, Long payValue, Boolean state)
    {
        this.id = id;
        this.payValue = payValue;
        this.state = state;
    }
}
