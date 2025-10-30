package com.fulldevcode.ecommerce.backend.infraestructure.Interface;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ProductDetailsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.models.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface IProduct extends JpaRepository<ProductEntity, Integer> {

    @Query("""
            SELECT p FROM ProductEntity p
            WHERE p.id != :id AND p.name = :name
            """)
    Optional<ProductEntity> FindByNameAndId(@Param("id") Integer id, @Param("name") String name);

    Optional<ProductEntity> findByName (String name);

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.ProductDetailsDTO (
                po.id,
                po.name,
                po.description,
                po.stock,
                po.sale,
                po.salePrice,
                ca.name,
                po.image
            ) FROM ProductEntity po
              JOIN po.category ca
            """)
    List<ProductDetailsDTO> GetAllProducts();
}
