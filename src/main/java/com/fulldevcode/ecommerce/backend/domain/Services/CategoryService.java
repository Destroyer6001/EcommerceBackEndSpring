package com.fulldevcode.ecommerce.backend.domain.Services;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ApiResponseDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.CategoryDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.ICategory;
import com.fulldevcode.ecommerce.backend.infraestructure.models.CategoryEntity;
import jakarta.persistence.PersistenceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final ICategory categoryResposity;

    public CategoryService(ICategory categoryRepo){
        this.categoryResposity = categoryRepo;
    }

    public ApiResponseDTO<List<CategoryDTO>> IndexCategories()
    {
        try
        {
            List<CategoryDTO> Categories = categoryResposity.findALlCategories();
            return  ApiResponseDTO.success("Lista de categorias obtenidas correctamente", Categories);

        }
        catch (PersistenceException | IllegalArgumentException ex)
        {
            String message = "Ha ocurrido un error" + ex.getMessage();
            return  ApiResponseDTO.error(message);
        }
        catch (Exception ex)
        {
            String message = "Ha ocurrido un error " + ex.getMessage();
            return  ApiResponseDTO.error(message);
        }
    }

    public  ApiResponseDTO<CategoryDTO> FindCategory(Integer id)
    {
        try
        {
            Optional<CategoryEntity> CategoryOptional = categoryResposity.findById(id);

            if (!CategoryOptional.isPresent())
            {
                return ApiResponseDTO.error("La categoria consultada no se encuentra registrada en el sistema");
            }

            CategoryDTO category = new CategoryDTO();
            category.setId(CategoryOptional.get().getId());
            category.setName(CategoryOptional.get().getName());
            category.setDescription(CategoryOptional.get().getDescription());

            return ApiResponseDTO.success("Categoria obtenida con exito", category);

        }
        catch (PersistenceException | IllegalArgumentException ex)
        {
            String message = "Ha ocurrido un error" + ex.getMessage();
            return  ApiResponseDTO.error(message);
        }
        catch (Exception ex)
        {
            String message = "Ha ocurrido un error " + ex.getMessage();
            return  ApiResponseDTO.error(message);
        }
    }

    public  ApiResponseDTO<CategoryDTO> Create(CategoryDTO categoryDTO)
    {
        try
        {
            Optional<CategoryEntity> FindCategoryName = categoryResposity.findByName(categoryDTO.getName());

            if (FindCategoryName.isPresent())
            {
                return ApiResponseDTO.error("La categoria ya se encuentra registrada en el sistema");
            }

            CategoryEntity category = new CategoryEntity();
            category.setName(categoryDTO.getName());
            category.setDescription(categoryDTO.getDescription());
            CategoryEntity categorySave = categoryResposity.save(category);

            return ApiResponseDTO.success("La categoria ha sido creado con exito", categoryDTO);
        }
        catch (PersistenceException | IllegalArgumentException ex)
        {
            String message = "Ha ocurrido un error" + ex.getMessage();
            return  ApiResponseDTO.error(message);
        }
        catch (Exception ex)
        {
            String message = "Ha ocurrido un error " + ex.getMessage();
            return  ApiResponseDTO.error(message);
        }
    }

    public  ApiResponseDTO<CategoryDTO> Edit(Integer id, CategoryDTO CategoryDTO)
    {
        try
        {
            Optional<CategoryEntity> CategoryEditSearch = categoryResposity.findByIdAndName(id, CategoryDTO.getName());
            CategoryEntity CategoryUpdate = new CategoryEntity();

            if (CategoryEditSearch.isPresent())
            {
                return ApiResponseDTO.error("La categoria ya se encuentra registrada en el sistema");
            }

            Optional<CategoryEntity> Category = categoryResposity.findById(id);

            if (Category.isPresent())
            {
                CategoryUpdate = Category.get();
            }
            else
            {
                return ApiResponseDTO.error("La categoria no se encuentra registrada en el sistema");
            }

            CategoryUpdate.setName(CategoryDTO.getName());
            CategoryUpdate.setDescription(CategoryDTO.getDescription());
            CategoryEntity ResultCategory = categoryResposity.save(CategoryUpdate);

            return  ApiResponseDTO.success("La categoria ha sido actualizada con exito", CategoryDTO);
        }
        catch (PersistenceException | IllegalArgumentException ex)
        {
            String message = "Ha ocurrido un error" + ex.getMessage();
            return  ApiResponseDTO.error(message);
        }
        catch (Exception ex)
        {
            String message = "Ha ocurrido un error " + ex.getMessage();
            return  ApiResponseDTO.error(message);
        }
    }

    public ApiResponseDTO<CategoryDTO> Delete(Integer id)
    {
        try
        {
            Optional<CategoryEntity> CategorySearch = categoryResposity.findById(id);
            CategoryEntity Category = new CategoryEntity();

            if (CategorySearch.isPresent())
            {
                Category = CategorySearch.get();
            }
            else
            {
                return ApiResponseDTO.error("La categoria no se encuentra registrada en el sistema");
            }

            CategoryDTO categoryDTO = new CategoryDTO(Category.getId(), Category.getName(), Category.getDescription());
            categoryResposity.delete(Category);
            return ApiResponseDTO.success("La categoria ha sido eliminada con exito", categoryDTO);
        }
        catch (PersistenceException | IllegalArgumentException ex)
        {
            String message = "Ha ocurrido un error" + ex.getMessage();
            return  ApiResponseDTO.error(message);
        }
        catch (Exception ex)
        {
            String message = "Ha ocurrido un error " + ex.getMessage();
            return  ApiResponseDTO.error(message);
        }
    }
}
