package com.fulldevcode.ecommerce.backend.infraestructure.Interface;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportTotalsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ShipmentsDetails;
import com.fulldevcode.ecommerce.backend.infraestructure.models.ShipmentEntity;
import com.fulldevcode.ecommerce.backend.infraestructure.models.ShipmentState;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IShipment extends JpaRepository<ShipmentEntity, Integer> {

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.ShipmentsDetails(
                sh.id,
                CAST(sh.state AS string),
                sh.date,
                sh.deliveryDate
            )
            FROM ShipmentEntity sh
            WHERE sh.user.id = :id
            """)
    List<ShipmentsDetails> FindShipmentsByUserId(@Param("id") Integer id);

    @Query("""
            SELECT sh
            FROM ShipmentEntity sh
            WHERE sh.order.id = :id
            and sh.state in :shipmentState
            """)
    Optional<ShipmentEntity> FindByOrderId(@Param("id") Integer id, @Param("shipmentState") Collection<ShipmentState> shipmentState);

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportTotalsDTO
            (
                CONCAT(us.firstname, ' ', us.lastname),
                COUNT(sh)
            )
            FROM ShipmentEntity sh
            JOIN sh.user us
            WHERE sh.state = :state
            GROUP BY us.id, us.firstname, us.lastname
            ORDER BY COUNT (sh) DESC
            """)
    List<ReportTotalsDTO> SearchTotalDeliveriesUser(PageRequest pageable, @Param("state") ShipmentState state);

}
