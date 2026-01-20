package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class PayslipDetails {

    private Integer id;

    private Long total;

    private LocalDateTime paymentDate;

    private List<PaymentDetails> payments;

    public PayslipDetails(Integer id, Long total, LocalDateTime paymentDate)
    {
        this.id = id;
        this.total = total;
        this.paymentDate = paymentDate;
    }
}
