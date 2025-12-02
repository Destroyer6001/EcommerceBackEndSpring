package com.fulldevcode.ecommerce.backend.domain.Services;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.*;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IOrder;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IOrderProducts;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IProduct;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IUser;
import com.fulldevcode.ecommerce.backend.infraestructure.models.*;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
            order.setState(orderEntity.getState().toString());
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
    public ApiResponseDTO<OrderDTO> CreateOrder (OrderDTO orderDTO)
    {
        try
        {
            List<String> Errors = ValidateStock(orderDTO.getOrderProducts());

            if (!Errors.isEmpty())
            {
                String mensagge = String.join("\n", Errors);
                return ApiResponseDTO.error(mensagge);
            }

            int UserId = this.GetIdByUser();
            Optional<UserEntity> userEntity = userRepository.findById(UserId);
            List<ProductEntity> products = productRespository.findAll();

            if (userEntity.isEmpty())
            {
                return ApiResponseDTO.error("El usuario no se encuentra registrado en el sistema");
            }

            OrderEntity order = new OrderEntity();
            order.setUser(userEntity.get());
            order.setTotal(orderDTO.getTotal());
            order.setOrderDate(LocalDateTime.now());
            order.setState(OrderState.PENDING);

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

            return ApiResponseDTO.success("Se ha creado con exito la orden", orderDTO);

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
    public ApiResponseDTO<ChangeStateDTO> ChangeStateOrder(ChangeStateDTO stateDTO)
    {
        try
        {
            System.out.println(stateDTO);
            int userId = GetIdByUser();
            Optional<UserEntity> UserResponse = userRepository.findById(userId);
            Optional<OrderEntity> Order = orderRepository.FindByIdOrder(stateDTO.getOrderId());
            String message = "";

            if (UserResponse.isEmpty())
            {
                message = "El usuario no se encuentra registado en el sistema";
                return ApiResponseDTO.error(message);
            }

            if (Order.isEmpty())
            {
                message = "La orden no se encuentra registrada en el sistema";
                return ApiResponseDTO.error(message);
            }

            if (Order.get().getState() != OrderState.PENDING)
            {
                message = "No puedes cambiar el estado de una orden que ya se encuentra completada o cancelada";
                return ApiResponseDTO.error(message);
            }

            if (stateDTO.getState() == 2 && UserResponse.get().getUserType() == UserType.USER)
            {
                message = "Un usuario normal no puede modificar el estado a completado";
                return ApiResponseDTO.error(message);
            }

            UserEntity User = UserResponse.get();

            if (stateDTO.getState() == 3 && User.getUserType() == UserType.USER)
            {
                LocalDateTime dateActually = LocalDateTime.now();
                long dayDiff = ChronoUnit.DAYS.between(Order.get().getOrderDate(), dateActually);

                if (dayDiff > 5)
                {
                    message = "Lo sentimos pero para poder realizar una cancelacion debe ser antes de que se cumplan 5 dias de la orden";
                    return ApiResponseDTO.error(message);
                }
            }

            OrderEntity OrderUpdate = Order.get();
            OrderState state = stateDTO.getState() == 2 ? OrderState.COMPLETED : OrderState.CANCEL;
            OrderUpdate.setState(state);
            orderRepository.save(OrderUpdate);

            if (stateDTO.getState() == 3)
            {
                List<ProductEntity> Products = productRespository.findAll();

                for(OrdersProductsEntity ordersProducts : OrderUpdate.getOrdersProducts())
                {
                    ProductEntity product = Products.stream().filter(p -> p.getId().equals(ordersProducts.getProduct().getId()))
                            .findFirst()
                            .orElse(null);

                    int stock = product.getStock() + ordersProducts.getStock();
                    product.setStock(stock);
                    productRespository.save(product);
                }
            }

            return ApiResponseDTO.success("Se ha actualizado el estado con exito", stateDTO);
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

    public int GetIdByUser()
    {
        UserEntity user = (UserEntity) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return user.getId();
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

    public ApiResponseDTO<List<ReportProductTotalSalesDTO>> ProductTotalSales()
    {
        try
        {
            List<ReportProductTotalSalesDTO> totalSales = orderProductsRespository.SearchTotalSalesProducts(PageRequest.of(0, 5));
            return ApiResponseDTO.success("Lista de ganancias total de venta por productos obtenida correctamente", totalSales);
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

    public ApiResponseDTO<List<ReportProductsMaxSalesDTO>> ProductMaxSales()
    {
        try
        {
            List<ReportProductsMaxSalesDTO> maxSales = orderProductsRespository.SearchMaxSalesProduct(PageRequest.of(0 , 5));
            return  ApiResponseDTO.success("Lista de ventas totales por producto obnida correctamente", maxSales);
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

    public ApiResponseDTO<List<ReportStatesProductsDTO>> OrdersTotalState()
    {
        try
        {
            List<ReportStatesProductsDTO> totalStates = orderRepository.SearchTotalOrderStates();
            return ApiResponseDTO.success("Ordenes totales por estado obtenido correctamente", totalStates);
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

    public ApiResponseDTO<List<CategoriesTotalSalesDTO>> CategoriesTotalSales()
    {
        try
        {
            List<CategoriesTotalSalesDTO> categoriesTotalSales = orderProductsRespository.SearchCategoriesTotalSales(PageRequest.of(0 , 5));
            return ApiResponseDTO.success("Lista de categorias con mas ganancias obtenidas correctamente", categoriesTotalSales);

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

    public ApiResponseDTO<List<CategoriesMaxSalesDTO>> CategoriesMaxSales()
    {
        try
        {
            List<CategoriesMaxSalesDTO> categoriesMaxSale = orderProductsRespository.SearchCategoriesMaxSales(PageRequest.of(0, 5));
            return ApiResponseDTO.success("Lista de numero de ventas por categoria obtenido correctamente", categoriesMaxSale);
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
