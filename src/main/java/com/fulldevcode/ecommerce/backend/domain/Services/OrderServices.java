package com.fulldevcode.ecommerce.backend.domain.Services;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.*;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IOrder;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IOrderProducts;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IProduct;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IUser;
import com.fulldevcode.ecommerce.backend.infraestructure.models.OrderEntity;

import com.fulldevcode.ecommerce.backend.infraestructure.models.OrdersProductsEntity;
import com.fulldevcode.ecommerce.backend.infraestructure.models.ProductEntity;
import com.fulldevcode.ecommerce.backend.infraestructure.models.UserEntity;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServices {

    private final IOrder orderRepository;
    private final IProduct productRespository;
    private final IOrderProducts orderProductsRespository;
    private final IUser userRepository;

    public OrderServices(IOrder orderRepo, IProduct productRepo, IOrderProducts orderProductsRepo, IUser userRepo)
    {
        this.orderRepository = orderRepo;
        this.productRespository = productRepo;
        this.orderProductsRespository = orderProductsRepo;
        this.userRepository = userRepo;
    }

    public ApiResponseDTO<List<OrderDetailsDTO>> FinAllOrders()
    {
        try
        {
            List<OrderDetailsDTO> orderDetails = orderRepository.FindAllOrders();
            return ApiResponseDTO.success("Listado de ordenes obtenido con exito", orderDetails);
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

    public ApiResponseDTO<List<OrderDetailsDTO>> FindAllOrdersUser(Integer userId)
    {
        try
        {
            List<OrderDetailsDTO> orderDetailsUser = orderRepository.FindAllOrdersUser(userId);
            return ApiResponseDTO.success("Se ha obtenido el listado de ordenes por usuario", orderDetailsUser);
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

    public ApiResponseDTO<OrderUserDTO> FindDetailsOrder(Integer id)
    {
        try
        {
            Optional<OrderEntity> orderDetailsEntity = orderRepository.FindByIdOrder(id);

            if (orderDetailsEntity.isEmpty())
            {
                return ApiResponseDTO.error("La orden no ha sido registrada en el sistema");
            }

            OrderEntity orderEntity = orderDetailsEntity.get();

            List<OrderProductsUserDTO> orderDetails = orderEntity.getOrdersProducts().stream()
                    .map(details -> new OrderProductsUserDTO(
                            details.getProduct().getName(),
                            details.getStock(),
                            details.getSalePrice()
                    )).toList();


            OrderUserDTO order = new OrderUserDTO();

            order.setId(orderEntity.getId());
            order.setUsername(orderEntity.getUser().getFirstname() + " " + orderEntity.getUser().getLastname());
            order.setOrderDate(orderEntity.getOrderDate());
            order.setTotal(orderEntity.getTotal());
            order.setDetailsUser(orderDetails);

            return ApiResponseDTO.success("Se ha obtenido con exito la informacion de la orden", order);

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

    @Transactional
    public ApiResponseDTO<OrderEntity> CreateOrder (OrderDTO orderDTO)
    {
        try
        {
            List<String> Errors = ValidateStock(orderDTO.getOrderProducts());

            if (!Errors.isEmpty())
            {
                String mensagge = String.join("\n", Errors);
                return ApiResponseDTO.error(mensagge);
            }

            Optional<UserEntity> userEntity = userRepository.findById(orderDTO.getUserId());
            List<ProductEntity> products = productRespository.findAll();

            if (userEntity.isEmpty())
            {
                return ApiResponseDTO.error("El usuario no se encuentra registrado en el sistema");
            }

            OrderEntity order = new OrderEntity();
            order.setUser(userEntity.get());
            order.setTotal(orderDTO.getTotal());
            order.setOrderDate(LocalDateTime.now());

            OrderEntity orderResponse = orderRepository.save(order);

            for (OrderProductsDTO orderProduct : orderDTO.getOrderProducts())
            {
                ProductEntity product = products.stream().filter(p -> p.getId().equals(orderProduct.getProductId()))
                        .findFirst()
                        .orElse(null);

                OrdersProductsEntity orderProductEntity = new OrdersProductsEntity();
                orderProductEntity.setOrder(orderResponse);
                orderProductEntity.setStock(orderProduct.getStock());
                orderProductEntity.setSalePrice(orderProduct.getSalePrice());
                orderProductEntity.setProduct(product);

                orderProductsRespository.save(orderProductEntity);

                Integer stock = product.getStock() - orderProduct.getStock();
                product.setStock(stock);
                productRespository.save(product);
            }

            return ApiResponseDTO.success("Se ha creado con exito la orden", orderResponse);

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

    public List<String> ValidateStock (List<OrderProductsDTO> orderproducts)
    {
        List<ProductDetailsDTO> productDetails = productRespository.GetAllProducts();
        List<String> productsNotStock = new ArrayList<>();

        for (OrderProductsDTO productsDTO : orderproducts)
        {
            ProductDetailsDTO product = productDetails.stream().filter(p -> p.getId().equals(productsDTO.getProductId()))
                    .findFirst()
                    .orElse(null);

            if (product == null)
            {
                productsNotStock.add("Uno de los productos seleccionados no existe en el sistema");
            }

            if (product.getStock() < productsDTO.getStock())
            {
                String mensaje = "El producto: " + product.getName() + "no tiene las existencias necesarias";
                productsNotStock.add(mensaje);
            }
        }

        return  productsNotStock;
    }
}
