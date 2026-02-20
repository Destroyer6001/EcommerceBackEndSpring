package com.fulldevcode.ecommerce.backend.infraestructure.Interface;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.PayslipDetails;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportTotalsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.models.PayslipEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IPayslip extends JpaRepository<PayslipEntity, Integer> {

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.PayslipDetails(
                py.id,
                py.total,
                py.paymentDate
            )
            FROM PayslipEntity py
            WHERE py.user.id = :id
            """)
    List<PayslipDetails> FindPaySlipsByUserId(@Param("id") Integer id);

    @Query("""
            SELECT DISTINCT py
            FROM PayslipEntity py
            JOIN FETCH py.payments pa
            JOIN FETCH pa.shipment
            WHERE py.id = :id 
            """)
    Optional<PayslipEntity> FindPaySlipDetailsById(@Param("id") Integer id);

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportTotalsDTO
            (
                CONCAT(us.firstname, ' ', us.lastname),
                SUM(py.total)
            )
            FROM PayslipEntity py
            JOIN py.user us
            GROUP BY us.id, us.firstname, us.lastname
            ORDER BY SUM(py.total) DESC
            """)
    List<ReportTotalsDTO> SearchTotalPayDeliveriesUser(PageRequest pageable);


}
