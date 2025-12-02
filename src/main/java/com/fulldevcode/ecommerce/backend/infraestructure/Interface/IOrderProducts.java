package com.fulldevcode.ecommerce.backend.infraestructure.Interface;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.CategoriesMaxSalesDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.CategoriesTotalSalesDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportProductTotalSalesDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportProductsMaxSalesDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.models.OrdersProductsEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IOrderProducts extends JpaRepository<OrdersProductsEntity, Integer> {

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportProductTotalSalesDTO (
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
    List<ReportProductTotalSalesDTO> SearchTotalSalesProducts (PageRequest pageable);

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportProductsMaxSalesDTO (
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
    List<ReportProductsMaxSalesDTO> SearchMaxSalesProduct(PageRequest pageable);

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.CategoriesTotalSalesDTO
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
    List<CategoriesTotalSalesDTO> SearchCategoriesTotalSales(PageRequest pageable);

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.CategoriesMaxSalesDTO
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
    List<CategoriesMaxSalesDTO> SearchCategoriesMaxSales(PageRequest pageable);
}
