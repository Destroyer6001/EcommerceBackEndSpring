package com.fulldevcode.ecommerce.backend.domain.Services;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ApiResponseDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.InventoryAdjustmentDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.InventoryAdjustmentDetailsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.OrderDetailsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IInventoryAdjustment;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IOrder;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IOrderProducts;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IProduct;
import com.fulldevcode.ecommerce.backend.infraestructure.models.InventoryAdjustmentEntity;
import com.fulldevcode.ecommerce.backend.infraestructure.models.ProductEntity;
import jakarta.persistence.PersistenceException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryAdjustmentService {

    private final IInventoryAdjustment inventoryAdjustmentRepository;
    private final IProduct productRepository;
    private final IOrder orderRespository;

    public InventoryAdjustmentService(IInventoryAdjustment inventoryAdjustmentRepo, IProduct productRepo, IOrder orderRepo)
    {
        this.inventoryAdjustmentRepository = inventoryAdjustmentRepo;
        this.productRepository = productRepo;
        this.orderRespository = orderRepo;
    }

    public ApiResponseDTO<List<InventoryAdjustmentDetailsDTO>> FindAllInventoryAdjustment(Integer id)
    {
        try
        {
            List<InventoryAdjustmentDetailsDTO> inventoryAdjustments = inventoryAdjustmentRepository.GetALlProducts(id);
            return  ApiResponseDTO.success("Se a obtenido con exito los ajustes de inventario correspondientes", inventoryAdjustments);
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

    public ApiResponseDTO<InventoryAdjustmentDTO> GetById(Integer id)
    {
        try
        {
            Optional<InventoryAdjustmentEntity> inventoryEntity = inventoryAdjustmentRepository.findById(id);

            if (inventoryEntity.isEmpty())
            {
                return ApiResponseDTO.error("El ajuste buscado no ha sido registrado en el sistema");
            }

            InventoryAdjustmentDTO inventoryAdjustment = new InventoryAdjustmentDTO();
            inventoryAdjustment.setId(inventoryEntity.get().getId());
            inventoryAdjustment.setStock(inventoryEntity.get().getStock());
            inventoryAdjustment.setPurchasePrice(inventoryEntity.get().getPurchasePrice());
            inventoryAdjustment.setProductId(inventoryEntity.get().getProduct().getId());

            return ApiResponseDTO.success("Se ha obtenido con exito la informacion del ajuste de inventario", inventoryAdjustment);
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

    public ApiResponseDTO<InventoryAdjustmentDTO> Create (InventoryAdjustmentDTO inventoryAdjustmentDTO)
    {
        try
        {
            Optional<ProductEntity> productEntity = productRepository.findById(inventoryAdjustmentDTO.getProductId());

            if (productEntity.isEmpty())
            {
                return ApiResponseDTO.error("El producto seleccionado no se encuentra registrado en el sistema");
            }

            Integer FinalStock = productEntity.get().getStock() + inventoryAdjustmentDTO.getStock();

            InventoryAdjustmentEntity inventoryAdjustment = new InventoryAdjustmentEntity();
            inventoryAdjustment.setStock(inventoryAdjustmentDTO.getStock());
            inventoryAdjustment.setPurchasePrice(inventoryAdjustmentDTO.getPurchasePrice());
            inventoryAdjustment.setAdjustmentDate(LocalDateTime.now());
            inventoryAdjustment.setProduct(productEntity.get());

            inventoryAdjustmentRepository.save(inventoryAdjustment);

            ProductEntity product = productEntity.get();
            product.setStock(FinalStock);

            productRepository.save(product);

            return  ApiResponseDTO.success("Se ha creado con exito el ajuste de inventario", inventoryAdjustmentDTO);
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

    public ApiResponseDTO<InventoryAdjustmentDTO> Edit (Integer id, InventoryAdjustmentDTO inventoryAdjustmentDTO)
    {
        try
        {
            Optional<InventoryAdjustmentEntity> inventoryAdjustmentEntity = inventoryAdjustmentRepository.findById(id);

            if (inventoryAdjustmentEntity.isEmpty())
            {
                return ApiResponseDTO.error("El ajuste de inventario no se encuentra registrado en el sistema");
            }

            Optional<ProductEntity> productEntity = productRepository.findById(inventoryAdjustmentDTO.getProductId());

            if (productEntity.isEmpty())
            {
                return  ApiResponseDTO.error("El producto seleccionado no se encuentra registrado en el sistema");
            }

            InventoryAdjustmentEntity inventoryAdjustment = inventoryAdjustmentEntity.get();

            List<OrderDetailsDTO> ordersProducts = orderRespository.FindProductIdAndDate(inventoryAdjustment.getProduct().getId(), inventoryAdjustment.getAdjustmentDate());

            if (!ordersProducts.isEmpty())
            {
                return ApiResponseDTO.error("No puedes editar este ajuste de inventario debido a que se realizaron movimientos de este producto despues de su creacion");
            }

            Integer finalStock = (productEntity.get().getStock() - inventoryAdjustmentEntity.get().getStock()) + inventoryAdjustmentDTO.getStock();

            inventoryAdjustment.setPurchasePrice(inventoryAdjustmentDTO.getPurchasePrice());
            inventoryAdjustment.setStock(inventoryAdjustmentDTO.getStock());
            inventoryAdjustment.setProduct(productEntity.get());

            inventoryAdjustmentRepository.save(inventoryAdjustment);

            ProductEntity product = productEntity.get();
            product.setStock(finalStock);

            productRepository.save(product);

            return ApiResponseDTO.success("El ajuste de inventario fue actualizado con exito", inventoryAdjustmentDTO);

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

    public ApiResponseDTO<InventoryAdjustmentDTO> Delete (Integer id)
    {
        try
        {
            Optional<InventoryAdjustmentEntity> inventoryAdjustmentEntity = inventoryAdjustmentRepository.findById(id);

            if (inventoryAdjustmentEntity.isEmpty())
            {
                return ApiResponseDTO.error("El ajuste de inventario no se encuentra registrado en el sistema");
            }

            InventoryAdjustmentEntity inventoryAdjustment = inventoryAdjustmentEntity.get();

            List<OrderDetailsDTO> orderProducts = orderRespository.FindProductIdAndDate(inventoryAdjustment.getProduct().getId(), inventoryAdjustment.getAdjustmentDate());

            if (!orderProducts.isEmpty())
            {
                return ApiResponseDTO.error("No puedes eliminar este ajuste de inventario debido a que se realizaron movimientos de este producto despues de su creacion");
            }

            Optional<ProductEntity> productEntity = productRepository.findById(inventoryAdjustment.getProduct().getId());

            ProductEntity product = productEntity.get();

            Integer finalStock = product.getStock() - inventoryAdjustment.getStock();

            inventoryAdjustmentRepository.delete(inventoryAdjustment);

            product.setStock(finalStock);

            productRepository.save(product);

            InventoryAdjustmentDTO inventoryDTO = new InventoryAdjustmentDTO();
            inventoryDTO.setStock(inventoryAdjustment.getStock());
            inventoryDTO.setPurchasePrice(inventoryAdjustment.getPurchasePrice());
            inventoryDTO.setProductId(inventoryAdjustment.getProduct().getId());

            return ApiResponseDTO.success("Se ha eliminado con exito el ajuste de inventario", inventoryDTO);

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
