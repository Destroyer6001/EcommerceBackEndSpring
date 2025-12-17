package com.fulldevcode.ecommerce.backend.application.controllers;

import com.fulldevcode.ecommerce.backend.domain.Services.CategoryService;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ApiResponseDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.CategoryDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.models.CategoryEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryServ) {
        this.categoryService = categoryServ;
    }

    @GetMapping("/getAll")
    public ApiResponseDTO<List<CategoryDTO>> GetALlCategories ()
    {
        ApiResponseDTO<List<CategoryDTO>> Categories = categoryService.IndexCategories();
        return Categories;
    }

    @GetMapping("/{id}")
    public  ApiResponseDTO<CategoryDTO> GetCategory(@PathVariable Integer id)
    {
        ApiResponseDTO<CategoryDTO> Category = categoryService.FindCategory(id);
        return Category;
    }

    @PostMapping
    public  ApiResponseDTO<CategoryDTO> CreateCategory(@RequestBody CategoryDTO CategoryDTO)
    {
        ApiResponseDTO<CategoryDTO> Category = categoryService.Create(CategoryDTO);
        return Category;
    }

    @PutMapping("/{id}")
    public  ApiResponseDTO<CategoryDTO> UpdateCategory(@PathVariable Integer id, @RequestBody CategoryDTO CategoryDTO)
    {
        ApiResponseDTO<CategoryDTO> Category = categoryService.Edit(id, CategoryDTO);
        return Category;
    }

    @DeleteMapping("/{id}")
    public  ApiResponseDTO<CategoryDTO> DeleteCategory(@PathVariable Integer id)
    {
        ApiResponseDTO<CategoryDTO> Category = categoryService.Delete(id);
        return Category;
    }
}
