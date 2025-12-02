package com.fulldevcode.ecommerce.backend.application.controllers;

import com.fulldevcode.ecommerce.backend.domain.Services.OrderServices;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.*;
import com.fulldevcode.ecommerce.backend.infraestructure.models.OrderEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.ls.LSInput;

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
    public ApiResponseDTO<OrderDTO> CreateOrder(@RequestBody OrderDTO orderDTO)
    {
        ApiResponseDTO<OrderDTO> Order = orderServices.CreateOrder(orderDTO);
        return Order;
    }

    @PostMapping("/changeState")
    public  ApiResponseDTO<ChangeStateDTO> ChangeState(@RequestBody ChangeStateDTO stateDTO)
    {
        ApiResponseDTO<ChangeStateDTO> State = orderServices.ChangeStateOrder(stateDTO);
        return State;
    }

    @GetMapping("/totalSalesProduct")
    public ApiResponseDTO<List<ReportTotalsDTO>> TotalSalesProduct ()
    {
        ApiResponseDTO<List<ReportTotalsDTO>> totalSales = orderServices.ProductTotalSales();
        return totalSales;
    }

    @GetMapping("/maxSalesProduct")
    public ApiResponseDTO<List<ReportTotalsDTO>> MaxSalesProduct ()
    {
        ApiResponseDTO<List<ReportTotalsDTO>> maxSales = orderServices.ProductMaxSales();
        return maxSales;
    }

    @GetMapping("/ordersForState")
    public ApiResponseDTO<List<ReportTotalsDTO>> OrdersStatesTotal()
    {
        ApiResponseDTO<List<ReportTotalsDTO>> ordersStates = orderServices.OrdersTotalState();
        return ordersStates;
    }

    @GetMapping("/totalSalesCategory")
    public ApiResponseDTO<List<ReportTotalsDTO>> TotalSalesCategory()
    {
        ApiResponseDTO<List<ReportTotalsDTO>> totalSales = orderServices.CategoriesTotalSales();
        return totalSales;
    }

    @GetMapping("/maxSalesCategory")
    public ApiResponseDTO<List<ReportTotalsDTO>> MaxSalesCategory()
    {
        ApiResponseDTO<List<ReportTotalsDTO>> maxSales = orderServices.CategoriesMaxSales();
        return maxSales;
    }
}
