package com.fulldevcode.ecommerce.backend.application.controllers;

import com.fulldevcode.ecommerce.backend.domain.Services.ProductServices;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ApiResponseDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ProductDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ProductDetailsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.models.ProductEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.ls.LSInput;

import java.util.List;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private final ProductServices productService;

    public ProductController (ProductServices serviceProduct)
    {
        this.productService = serviceProduct;
    }

    @GetMapping("/getAll")
    public ApiResponseDTO<List<ProductDetailsDTO>> GetAllProducts()
    {
        ApiResponseDTO<List<ProductDetailsDTO>> products = productService.GetAllProduct();
        return products;
    }

    @GetMapping("/{id}")
    public ApiResponseDTO<ProductDTO> GetByIdProduct(@PathVariable Integer id)
    {
        ApiResponseDTO<ProductDTO> product = productService.GetByIdProduct(id);
        return product;
    }

    @PostMapping
    public ApiResponseDTO<ProductEntity> CreateProduct(@RequestBody ProductDTO productDTO)
    {
        ApiResponseDTO<ProductEntity> product = productService.Create(productDTO);
        return product;
    }

    @PutMapping("/{id}")
    public ApiResponseDTO<ProductEntity> EditProduct(@PathVariable Integer id, @RequestBody ProductDTO productDTO)
    {
        ApiResponseDTO<ProductEntity> product = productService.Edit(id, productDTO);
        return product;
    }

    @DeleteMapping("/{id}")
    public  ApiResponseDTO<ProductEntity> DeleteProduct (@PathVariable Integer id)
    {
        ApiResponseDTO<ProductEntity> product = productService.Delete(id);
        return product;
    }
}
