package com.fulldevcode.ecommerce.backend.infraestructure.Interface;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.CategoryDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.models.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ICategory extends JpaRepository<CategoryEntity, Integer>
{
    @Query("""
            SELECT c FROM CategoryEntity c
            WHERE c.id != :id AND c.name = :name
            """)
    Optional<CategoryEntity> findByIdAndName (@Param("id") Integer id, @Param("name") String name);

    Optional<CategoryEntity> findByName(String name);

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.CategoryDTO(
                c.id,
                c.name,
                c.description
            )
            FROM CategoryEntity c""")
    List<CategoryDTO> findALlCategories();

}
