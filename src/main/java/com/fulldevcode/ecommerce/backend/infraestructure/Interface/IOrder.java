package com.fulldevcode.ecommerce.backend.infraestructure.Interface;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.OrderDetailsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportTotalsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.models.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IOrder extends JpaRepository<OrderEntity, Integer> {

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.OrderDetailsDTO
            (
                or.id,
                CONCAT(us.firstname, ' ', us.lastname),
                or.OrderDate,
                or.Total,
                us.address,
                CAST(or.State AS string)
            )
            FROM OrderEntity or
            JOIN or.user us
            ORDER BY or.OrderDate DESC
            """)
    List<OrderDetailsDTO> FindAllOrders();

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.OrderDetailsDTO
            (
                or.id,
                CONCAT(us.firstname, ' ', us.lastname),
                or.OrderDate,
                or.Total,
                us.address,
                CAST(or.State AS string)
            )
            FROM OrderEntity or
            JOIN or.user us
            WHERE us.id = :id
            ORDER BY or.OrderDate DESC
            """)
    List<OrderDetailsDTO> FindAllOrdersUser(@Param("id") Integer id);

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.OrderDetailsDTO
            (
                or.id,
                CONCAT(us.firstname, ' ', us.lastname),
                or.OrderDate,
                or.Total,
                us.address,
                CAST(or.State AS string)
            )
            FROM OrderEntity or
            JOIN or.user us
            JOIN or.ordersProducts op
            JOIN op.product pr
            WHERE pr.id = :productId AND or.OrderDate > :dateOrder
            """)
    List<OrderDetailsDTO> FindProductIdAndDate(@Param("productId") Integer productid, @Param("dateOrder")LocalDateTime dateOrder);

    @Query("""
            SELECT DISTINCT or
            FROM OrderEntity or
            JOIN FETCH or.user us
            JOIN FETCH or.ordersProducts op
            JOIN FETCH op.product pr
            WHERE or.id = :id
            """)
    Optional<OrderEntity> FindByIdOrder(@Param("id") Integer id);

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportTotalsDTO
            (
                CAST(ord.State AS string),
                COUNT(ord)
            )
            FROM OrderEntity ord
            GROUP BY ord.State
            """)
    List<ReportTotalsDTO> SearchTotalOrderStates();
}
