package com.fulldevcode.ecommerce.backend.application.controllers;

import com.fulldevcode.ecommerce.backend.domain.Services.PayslipServices;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ApiResponseDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.PayslipDetails;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportTotalsDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/payslips")
public class PayslipController {

    private final PayslipServices payslipService;

    public PayslipController(PayslipServices payslipServices)
    {
        this.payslipService = payslipServices;
    }

    @GetMapping("/getAllPayslipsUser/{id}")
    public ApiResponseDTO<List<PayslipDetails>> GetAllPayslipUser (@PathVariable Integer id)
    {
        ApiResponseDTO<List<PayslipDetails>> payslipUser = this.payslipService.GetPayslipsUserId(id);
        return payslipUser;
    }

    @GetMapping("/getById/{id}")
    public ApiResponseDTO<PayslipDetails> GetByIdPayslip(@PathVariable Integer id)
    {
        ApiResponseDTO<PayslipDetails> payslip = this.payslipService.GetPaySlipDetails(id);
        return payslip;
    }

    @PostMapping()
    public ApiResponseDTO<Integer> CreatePayslip()
    {
        ApiResponseDTO<Integer> payslip = this.payslipService.CreatePayslip();
        return payslip;
    }

    @GetMapping("/payslipUsersTotal")
    public ApiResponseDTO<List<ReportTotalsDTO>> GetPayslip()
    {
        ApiResponseDTO<List<ReportTotalsDTO>> TotalsPayslip = this.payslipService.GetTotalsValuePayDeliveriesUser();
        return TotalsPayslip;
    }

}
