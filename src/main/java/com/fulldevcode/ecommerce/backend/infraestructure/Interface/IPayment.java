package com.fulldevcode.ecommerce.backend.infraestructure.Interface;

import com.fulldevcode.ecommerce.backend.infraestructure.models.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPayment extends JpaRepository<PaymentEntity, Integer> {

    @Query("""
            SELECT pa
            FROM PaymentEntity pa
            WHERE pa.user.id = :id
            AND pa.state = false
            """)
    List<PaymentEntity> findByStateFalse(@Param("id") Integer id);
}
