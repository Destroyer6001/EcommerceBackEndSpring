package com.fulldevcode.ecommerce.backend.domain.Services;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ApiResponseDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ProductDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ProductDetailsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.ICategory;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IProduct;
import com.fulldevcode.ecommerce.backend.infraestructure.models.CategoryEntity;
import com.fulldevcode.ecommerce.backend.infraestructure.models.ProductEntity;
import jakarta.persistence.PersistenceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServices {

    private final IProduct productRepository;
    private final ICategory categoryRespository;

    public ProductServices(IProduct productRepo, ICategory categoryRepo)
    {
        this.productRepository = productRepo;
        this.categoryRespository = categoryRepo;
    }

    public ApiResponseDTO<List<ProductDetailsDTO>> GetAllProduct ()
    {
        try
        {
            List<ProductDetailsDTO> products = productRepository.GetAllProducts();
            return  ApiResponseDTO.success("Se ha obtenido con exito la lista de productos", products);
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

    public ApiResponseDTO<ProductDTO> GetByIdProduct(Integer id)
    {
        try
        {
            Optional<ProductEntity> productEntity = productRepository.findById(id);

            if (productEntity.isEmpty())
            {
                return ApiResponseDTO.error("El producto no se encuentra registrado en el sistema");
            }

            ProductDTO product = new ProductDTO();
            product.setId(productEntity.get().getId());
            product.setName(productEntity.get().getName());
            product.setDescription(productEntity.get().getDescription());
            product.setStock(productEntity.get().getStock());
            product.setSale(productEntity.get().getSale());
            product.setSalePrice(productEntity.get().getSalePrice());
            product.setCategoryId(productEntity.get().getCategory().getId());
            product.setCreatedDate(productEntity.get().getCreatedDate());
            product.setImagen(productEntity.get().getImage());

            return  ApiResponseDTO.success("Producto obtenido con exito", product);

        } catch (PersistenceException | IllegalArgumentException ex)
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

    public ApiResponseDTO<ProductDTO> Create (ProductDTO productDTO)
    {
        try
        {
            Optional<ProductEntity> productName = productRepository.findByName(productDTO.getName());

            if (productName.isPresent())
            {
                return ApiResponseDTO.error("Ya hay un producto registrado con ese nombre");
            }

            Optional<CategoryEntity> categoryEntity = categoryRespository.findById(productDTO.getCategoryId());

            if (categoryEntity.isEmpty())
            {
                return ApiResponseDTO.error("La categoria seleccionada para este producto no existe");
            }

            ProductEntity product = new ProductEntity();
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setStock(productDTO.getStock());
            product.setSale(productDTO.getSale());
            product.setSalePrice(productDTO.getSalePrice());
            product.setCategory(categoryEntity.get());
            product.setCreatedDate(productDTO.getCreatedDate());

            if (!productDTO.getImagen().isEmpty())
            {
                product.setImage(productDTO.getImagen());
            }

            ProductEntity productResponse = productRepository.save(product);

            return ApiResponseDTO.success("Producto creado con exito", productDTO);

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

    public ApiResponseDTO<ProductDTO> Edit (Integer id, ProductDTO productDTO)
    {
        try
        {
            Optional<ProductEntity> productName = productRepository.FindByNameAndId(id, productDTO.getName());

            if (productName.isPresent())
            {
                return ApiResponseDTO.error("Ya existe un producto registrado con ese nombre");
            }

            Optional<ProductEntity> productSearch = productRepository.findById(id);

            if (productSearch.isEmpty())
            {
                return ApiResponseDTO.error("El producto no se encuentra registrado en el sistema");
            }

            ProductEntity product = productSearch.get();

            Optional<CategoryEntity> categorySearch = categoryRespository.findById(productDTO.getCategoryId());

            if (categorySearch.isEmpty())
            {
                return ApiResponseDTO.error("La categoria seleccionada no existe");
            }

            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setSale(productDTO.getSale());
            product.setSalePrice(productDTO.getSalePrice());
            product.setCategory(categorySearch.get());
            product.setCreatedDate(productDTO.getCreatedDate());

            if (!productDTO.getImagen().isEmpty())
            {
                product.setImage(productDTO.getImagen());
            }

            productRepository.save(product);

            return ApiResponseDTO.success("Se ha actualizado con exito la informacion del producto", productDTO);

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

    public ApiResponseDTO<ProductDTO> Delete (Integer id) {
        try
        {
            Optional<ProductEntity> productSearch = productRepository.findById(id);

            if (productSearch.isEmpty())
            {
                return ApiResponseDTO.error("El producto no se encuentra registrado");
            }

            productRepository.delete(productSearch.get());

            ProductDTO product = new ProductDTO();
            product.setId(productSearch.get().getId());
            product.setName(productSearch.get().getName());
            product.setDescription(productSearch.get().getDescription());
            product.setSale(productSearch.get().getSale());
            product.setSalePrice(productSearch.get().getSalePrice());
            product.setStock(productSearch.get().getStock());
            product.setImagen(productSearch.get().getImage());
            product.setCreatedDate(productSearch.get().getCreatedDate());
            product.setCategoryId(productSearch.get().getCategory().getId());

            return ApiResponseDTO.success("El producto ha sido eliminado con exito", product);
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
