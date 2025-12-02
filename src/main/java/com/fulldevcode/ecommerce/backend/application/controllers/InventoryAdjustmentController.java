package com.fulldevcode.ecommerce.backend.application.controllers;

import com.fulldevcode.ecommerce.backend.domain.Services.InventoryAdjustmentService;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ApiResponseDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.InventoryAdjustmentDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.InventoryAdjustmentDetailsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.models.InventoryAdjustmentEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/inventoryAdjustments")
public class InventoryAdjustmentController {

    private final InventoryAdjustmentService inventoryAdjustmentService;

    public InventoryAdjustmentController(InventoryAdjustmentService inventoryService)
    {
        this.inventoryAdjustmentService = inventoryService;
    }

    @GetMapping("/getAll/{id}")
    public ApiResponseDTO<List<InventoryAdjustmentDetailsDTO>> GetAllInventoryAdjustments(@PathVariable Integer id)
    {
        ApiResponseDTO<List<InventoryAdjustmentDetailsDTO>> inventoryAdjustments = inventoryAdjustmentService.FindAllInventoryAdjustment(id);
        return inventoryAdjustments;
    }

    @GetMapping("/{id}")
    public ApiResponseDTO<InventoryAdjustmentDTO> FindById (@PathVariable Integer id)
    {
        ApiResponseDTO<InventoryAdjustmentDTO> inventoryAdjustment = inventoryAdjustmentService.GetById(id);
        return inventoryAdjustment;
    }

    @PostMapping
    public ApiResponseDTO<InventoryAdjustmentDTO> CreateInventoryAdjustment (@RequestBody InventoryAdjustmentDTO inventoryAdjustmentDTO)
    {
        ApiResponseDTO<InventoryAdjustmentDTO> inventoryAdjustment = inventoryAdjustmentService.Create(inventoryAdjustmentDTO);
        return inventoryAdjustment;
    }

    @PutMapping("/{id}")
    public ApiResponseDTO<InventoryAdjustmentDTO> EditInventoryAdjustment (@PathVariable Integer id, @RequestBody InventoryAdjustmentDTO inventoryAdjustmentDTO)
    {
        ApiResponseDTO<InventoryAdjustmentDTO> inventoryAdjustment = inventoryAdjustmentService.Edit(id, inventoryAdjustmentDTO);
        return inventoryAdjustment;
    }

    @DeleteMapping("/{id}")
    public ApiResponseDTO<InventoryAdjustmentDTO> DeleteInventoryAdjustment (@PathVariable Integer id) {
        ApiResponseDTO<InventoryAdjustmentDTO> inventoryAdjustment = inventoryAdjustmentService.Delete(id);
        return inventoryAdjustment;
    }
}
