package com.fulldevcode.ecommerce.backend.infraestructure.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="payments")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long payValue;

    private Boolean state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private ShipmentEntity shipment;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name= "payslip_id", nullable = true)
    private PayslipEntity payslip;
}
