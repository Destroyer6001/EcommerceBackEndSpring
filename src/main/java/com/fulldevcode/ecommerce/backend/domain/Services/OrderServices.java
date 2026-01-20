package com.fulldevcode.ecommerce.backend.domain.Services;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.*;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.*;
import com.fulldevcode.ecommerce.backend.infraestructure.models.*;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.hibernate.query.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
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
    private final IShipment shipmentRepository;

    public OrderServices(IOrder orderRepo, IProduct productRepo, IOrderProducts orderProductsRepo, IUser userRepo,IShipment shipmentRepo)
    {
        this.orderRepository = orderRepo;
        this.productRespository = productRepo;
        this.orderProductsRespository = orderProductsRepo;
        this.userRepository = userRepo;
        this.shipmentRepository = shipmentRepo;
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
            order.setAddress(orderEntity.getUser().getAddress());
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

            if (orderDTO.getOrderProducts().isEmpty())
            {
                return ApiResponseDTO.error("No se puede registrar una orden sin productos");
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
    public ApiResponseDTO<Integer> CancelOrder (Integer id)
    {
        try
        {
            int userId = GetIdByUser();

            Optional<UserEntity> UserResponse = userRepository.findById(userId);
            Optional<OrderEntity> Order = orderRepository.FindByIdOrder(id);
            Optional<ShipmentEntity> Shipment = shipmentRepository.FindByOrderId(id, List.of(ShipmentState.PENDING));

            String message = "";

            if (Shipment.isPresent())
            {
                message = "Ya hay una orden de entrega pendiente asociada al pedido por ende no se puede cancelar";
                return ApiResponseDTO.error(message);
            }

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

            UserEntity User = UserResponse.get();

            if (User.getUserType() == UserType.USER)
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
            OrderState state = OrderState.CANCEL;
            OrderUpdate.setState(state);
            orderRepository.save(OrderUpdate);

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

            return ApiResponseDTO.success("Se ha actualizado el estado con exito", OrderUpdate.getId());
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

    public ApiResponseDTO<List<ReportTotalsDTO>> ProductTotalSales()
    {
        try
        {
            List<ReportTotalsDTO> totalSales = orderProductsRespository.SearchTotalSalesProducts(PageRequest.of(0, 5));
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

    public ApiResponseDTO<List<ReportTotalsDTO>> ProductMaxSales()
    {
        try
        {
            List<ReportTotalsDTO> maxSales = orderProductsRespository.SearchMaxSalesProduct(PageRequest.of(0 , 5));
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

    public ApiResponseDTO<List<ReportTotalsDTO>> OrdersTotalState()
    {
        try
        {
            List<ReportTotalsDTO> totalStates = orderRepository.SearchTotalOrderStates();
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

    public ApiResponseDTO<List<ReportTotalsDTO>> CategoriesTotalSales()
    {
        try
        {
            List<ReportTotalsDTO> categoriesTotalSales = orderProductsRespository.SearchCategoriesTotalSales(PageRequest.of(0 , 5));
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

    public ApiResponseDTO<List<ReportTotalsDTO>> CategoriesMaxSales()
    {
        try
        {
            List<ReportTotalsDTO> categoriesMaxSale = orderProductsRespository.SearchCategoriesMaxSales(PageRequest.of(0, 5));
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

    public ApiResponseDTO<List<OrderDetailsDTO>> GetOrdersPendingDelivery()
    {
        try
        {
            List<OrderDetailsDTO> OrdersPending = this.orderRepository.FindAllPendingOrders(OrderState.PENDING);
            return ApiResponseDTO.success("Se ha obtenido el listado de las ordenes pendientes por entregar", OrdersPending);
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
