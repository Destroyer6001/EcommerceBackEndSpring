package com.fulldevcode.ecommerce.backend.application.controllers;

import com.fulldevcode.ecommerce.backend.domain.Services.ShipmentService;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ApiResponseDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportTotalsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ShipmentsDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentServices)
    {
        this.shipmentService = shipmentServices;
    }

    @GetMapping("/getAll/{id}")
    public ApiResponseDTO<List<ShipmentsDetails>> GetAllShipmentsUser (@PathVariable Integer id)
    {
        ApiResponseDTO<List<ShipmentsDetails>> ShipmentsUser = this.shipmentService.GetAllShipmentByUser(id);
        return ShipmentsUser;
    }

    @PostMapping("/{id}")
    public ApiResponseDTO<Integer> CreateShipment (@PathVariable Integer id)
    {
        ApiResponseDTO<Integer> shipment = this.shipmentService.CreateShipment(id);
        return shipment;
    }

    @PutMapping("/cancel/{id}")
    public ApiResponseDTO<Integer> CancelShipment (@PathVariable Integer id)
    {
        ApiResponseDTO<Integer> shipment = this.shipmentService.CancelShipment(id);
        return shipment;
    }

    @PutMapping("/confirm/{id}")
    public ApiResponseDTO<Integer> ConfirmShipment (@PathVariable Integer id)
    {
        ApiResponseDTO<Integer> shipment = this.shipmentService.CompletedShipment(id);
        return shipment;
    }

    @GetMapping("/deliveriesUsersTotals")
    public ApiResponseDTO<List<ReportTotalsDTO>> getDeliveriesUsers ()
    {
        ApiResponseDTO<List<ReportTotalsDTO>> deliveriesUsers = this.shipmentService.GetTotalsDeliveriesForUser();
        return deliveriesUsers;
    }
}
