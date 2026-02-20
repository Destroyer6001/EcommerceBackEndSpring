package com.fulldevcode.ecommerce.backend.domain.Services;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ApiResponseDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.PaymentDetails;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.PayslipDetails;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportTotalsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IPayment;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IPayslip;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IUser;
import com.fulldevcode.ecommerce.backend.infraestructure.models.PaymentEntity;
import com.fulldevcode.ecommerce.backend.infraestructure.models.PayslipEntity;
import com.fulldevcode.ecommerce.backend.infraestructure.models.UserEntity;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PayslipServices {

    private final IPayment PaymentRepository;
    private final IPayslip PayslipRepository;
    private final IUser UserRepository;

    public PayslipServices(IPayslip payslipRepo, IPayment paymentRepo, IUser userRepo)
    {
        this.PayslipRepository = payslipRepo;
        this.PaymentRepository = paymentRepo;
        this.UserRepository = userRepo;
    }

    public ApiResponseDTO<List<PayslipDetails>> GetPayslipsUserId(Integer id)
    {
        try
        {
            List<PayslipDetails> payslips = this.PayslipRepository.FindPaySlipsByUserId(id);
            return ApiResponseDTO.success("Se ha obtenido con exito la lista de liquidaciones", payslips);
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

    public ApiResponseDTO<PayslipDetails> GetPaySlipDetails(Integer id)
    {
        try
        {
            Optional<PayslipEntity> payslipDetails = this.PayslipRepository.FindPaySlipDetailsById(id);

            if (payslipDetails.isEmpty())
            {
                return ApiResponseDTO.error("El registro de la liquidacion seleccionada no se encuentra registrada en el sistema");
            }

            PayslipDetails payslip = new PayslipDetails();

            List<PaymentDetails> payments = payslipDetails.get().getPayments().stream()
                    .map(details -> new PaymentDetails(
                            details.getId(),
                            details.getPayValue(),
                            details.getState(),
                            details.getShipment().getDeliveryDate()
                    )).toList();

            payslip.setTotal(payslipDetails.get().getTotal());
            payslip.setId(payslipDetails.get().getId());
            payslip.setPaymentDate(payslipDetails.get().getPaymentDate());
            payslip.setPayments(payments);

            return ApiResponseDTO.success("Se ha optenido con exito la informacion de la liquidacion", payslip);
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
    public ApiResponseDTO<Integer> CreatePayslip()
    {
        try
        {
            int userId = this.GetUserId();
            String message = "";
            Optional<UserEntity> user = this.UserRepository.findById(userId);

            if (user.isEmpty())
            {
                message = "Lo sentimos pero el usuario no se encuentra registrado en el sistema";
                return ApiResponseDTO.error(message);
            }

            List<PaymentEntity> Payments = this.PaymentRepository.findByStateFalse(user.get().getId());

            if (Payments.isEmpty())
            {
                message = "El usuario actual no tiene colillas de pago pendientes por liquidar";
                return ApiResponseDTO.error(message);
            }

            Long total = Payments.stream().mapToLong(PaymentEntity::getPayValue).sum();

            PayslipEntity payslip = new PayslipEntity();
            payslip.setUser(user.get());
            payslip.setPaymentDate(LocalDateTime.now());
            payslip.setTotal(total);
            this.PayslipRepository.save(payslip);

            for (PaymentEntity payment: Payments)
            {
                payment.setState(true);
                payment.setPayslip(payslip);
                this.PaymentRepository.save(payment);
            }

            return ApiResponseDTO.success("Se ha creado con exito la liquidacion del pago", payslip.getId());

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

    public ApiResponseDTO<List<ReportTotalsDTO>> GetTotalsValuePayDeliveriesUser()
    {
        try
        {
            List<ReportTotalsDTO> totals = this.PayslipRepository.SearchTotalPayDeliveriesUser(PageRequest.of(0, 5));
            return ApiResponseDTO.success("Se ha encontrado con exito la lista de la ganancia total por domiciliario", totals);
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

    public int GetUserId()
    {
        UserEntity user = (UserEntity) SecurityContextHolder
                                        .getContext()
                                        .getAuthentication()
                                        .getPrincipal();
        return user.getId();
    }
}
