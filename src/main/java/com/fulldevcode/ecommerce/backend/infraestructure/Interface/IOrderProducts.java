package com.fulldevcode.ecommerce.backend.infraestructure.Interface;

import com.fulldevcode.ecommerce.backend.infraestructure.models.OrdersProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderProducts extends JpaRepository<OrdersProductsEntity, Integer> {
}
