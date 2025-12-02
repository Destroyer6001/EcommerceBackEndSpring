package com.fulldevcode.ecommerce.backend.infraestructure.Interface;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.*;
import com.fulldevcode.ecommerce.backend.infraestructure.models.OrdersProductsEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IOrderProducts extends JpaRepository<OrdersProductsEntity, Integer> {

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportTotalsDTO (
                pr.name,
                SUM ((orp.salePrice * orp.stock) - (pr.sale * orp.stock))
            )
            FROM OrdersProductsEntity orp
            JOIN orp.product pr
            JOIN orp.order ord
            WHERE ord.State = com.fulldevcode.ecommerce.backend.infraestructure.models.OrderState.COMPLETED
            GROUP BY pr.id, pr.name
            ORDER BY SUM ((orp.salePrice * orp.stock) - (pr.sale * orp.stock)) DESC
            """)
    List<ReportTotalsDTO> SearchTotalSalesProducts (PageRequest pageable);

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportTotalsDTO (
                pr.name,
                SUM(orp.stock)
            )
            FROM OrdersProductsEntity orp
            JOIN orp.product pr
            JOIN orp.order ord
            WHERE ord.State = com.fulldevcode.ecommerce.backend.infraestructure.models.OrderState.COMPLETED
            GROUP BY pr.id, pr.name
            ORDER BY SUM(orp.stock) DESC
            """)
    List<ReportTotalsDTO> SearchMaxSalesProduct(PageRequest pageable);

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportTotalsDTO
            (
                cat.name,
                SUM((orp.salePrice * orp.stock) - (pr.sale * orp.stock))
            )
            FROM OrdersProductsEntity orp
            JOIN orp.product pr
            JOIN orp.order ord
            JOIN pr.category cat
            WHERE ord.State = com.fulldevcode.ecommerce.backend.infraestructure.models.OrderState.COMPLETED
            GROUP BY cat.id, cat.name
            ORDER BY SUM((orp.salePrice * orp.stock) - (pr.sale * orp.stock)) DESC
            """)
    List<ReportTotalsDTO> SearchCategoriesTotalSales(PageRequest pageable);

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportTotalsDTO
            (
              cat.name,
              SUM(orp.stock)
            )
            FROM OrdersProductsEntity orp
            JOIN orp.product pr
            JOIN orp.order ord
            JOIN pr.category cat
            WHERE ord.State = com.fulldevcode.ecommerce.backend.infraestructure.models.OrderState.COMPLETED
            GROUP BY cat.id, cat.name
            ORDER BY SUM(orp.stock) DESC
            """)
    List<ReportTotalsDTO> SearchCategoriesMaxSales(PageRequest pageable);
}
