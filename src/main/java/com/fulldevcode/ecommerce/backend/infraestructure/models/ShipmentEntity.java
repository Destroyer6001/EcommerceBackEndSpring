package com.fulldevcode.ecommerce.backend.infraestructure.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Shipments")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private ShipmentState state;

    private LocalDateTime date;

    @Column(nullable = true)
    private LocalDateTime deliveryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @OneToOne(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private PaymentEntity payment;

}
