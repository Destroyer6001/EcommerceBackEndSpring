package com.fulldevcode.ecommerce.backend.application.controllers;

import com.fulldevcode.ecommerce.backend.domain.Services.OrderServices;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ApiResponseDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.OrderDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.OrderDetailsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.OrderUserDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.models.OrderEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
public class OrderController {

    private final OrderServices orderServices;

    public OrderController(OrderServices serviceOrder)
    {
        this.orderServices = serviceOrder;
    }

    @GetMapping("/getAllOrders")
    public ApiResponseDTO<List<OrderDetailsDTO>> GetAllOrders() {
        ApiResponseDTO<List<OrderDetailsDTO>> Orders = orderServices.FinAllOrders();
        return Orders;
    }

    @GetMapping("/getOrdersUser/{id}")
    public ApiResponseDTO<List<OrderDetailsDTO>> GetOrdersUser(@PathVariable Integer id)
    {
        ApiResponseDTO<List<OrderDetailsDTO>> Orders = orderServices.FindAllOrdersUser(id);
        return Orders;
    }

    @GetMapping("/orderById/{id}")
    public ApiResponseDTO<OrderUserDTO> GetOrderById(@PathVariable Integer id)
    {
        ApiResponseDTO<OrderUserDTO> Order = orderServices.FindDetailsOrder(id);
        return Order;
    }

    @PostMapping("/orderCreate")
    public ApiResponseDTO<OrderEntity> CreateOrder(@RequestBody OrderDTO orderDTO)
    {
        ApiResponseDTO<OrderEntity> Order = orderServices.CreateOrder(orderDTO);
        return Order;
    }
}
