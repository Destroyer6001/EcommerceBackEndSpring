package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor

public class PaymentDetails {

    private Integer id;

    private Long payValue;

    private Boolean state;

    private LocalDateTime payDay;

    public PaymentDetails (Integer id, Long payValue, Boolean state, LocalDateTime payDay)
    {
        this.id = id;
        this.payValue = payValue;
        this.state = state;
        this.payDay = payDay;
    }
}
