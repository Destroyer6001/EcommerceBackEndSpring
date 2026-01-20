package com.fulldevcode.ecommerce.backend.domain.Services;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ApiResponseDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ReportTotalsDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ShipmentsDetails;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IOrder;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IPayment;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IShipment;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IUser;
import com.fulldevcode.ecommerce.backend.infraestructure.models.*;
import jakarta.persistence.Convert;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShipmentService {

    private final IShipment ShipmentRepository;
    private final IOrder OrderRepository;
    private final IUser userRepository;
    private final IPayment PaymentRepository;

    public ShipmentService (IShipment ShipmentRepo, IOrder orderRepo, IUser userRepo, IPayment paymentRepo)
    {
        this.ShipmentRepository = ShipmentRepo;
        this.OrderRepository = orderRepo;
        this.userRepository = userRepo;
        this.PaymentRepository = paymentRepo;
    }

    public ApiResponseDTO<List<ShipmentsDetails>> GetAllShipmentByUser(Integer id)
    {
        try
        {
            List<ShipmentsDetails> shipmentsDetails = this.ShipmentRepository.FindShipmentsByUserId(id);
            return ApiResponseDTO.success("Se ha encontrado la lista de ordenes con exito", shipmentsDetails);
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
    public ApiResponseDTO<Integer> CreateShipment(Integer orderId)
    {
        try
        {
            String ErrorMessage = "";
            Optional<UserEntity> user = this.userRepository.findById(this.GetByUserId());
            Optional<OrderEntity> order = this.OrderRepository.FindByIdOrder(orderId);
            Optional<ShipmentEntity> shipmentExists = this.ShipmentRepository.FindByOrderId(orderId, List.of(ShipmentState.PENDING, ShipmentState.COMPLETED));

            if (user.isEmpty())
            {
                ErrorMessage = "El usuario seleccionado no se encuentra registrado en el sistema";
                return ApiResponseDTO.error(ErrorMessage);
            }

            if (shipmentExists.isPresent())
            {
                ErrorMessage = "No puedes crear un nuevo envio con este pedido";
                return ApiResponseDTO.error(ErrorMessage);
            }

            if (order.isEmpty())
            {
                ErrorMessage = "La orden seleccionada no se encuentra registrada en el sistema";
                return ApiResponseDTO.error(ErrorMessage);
            }

            ShipmentEntity shipment = new ShipmentEntity();
            shipment.setDate(LocalDateTime.now());
            shipment.setOrder(order.get());
            shipment.setUser(user.get());
            shipment.setState(ShipmentState.PENDING);
            this.ShipmentRepository.save(shipment);

            OrderEntity orderEntity = order.get();
            orderEntity.setState(OrderState.SEND);
            this.OrderRepository.save(orderEntity);

            return ApiResponseDTO.success("Se ha creado con exito la orden de entrega", shipment.getId());
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
    public ApiResponseDTO<Integer> CancelShipment(Integer shipmentId)
    {
        try
        {
            String ErrorMessage = "";
            Optional<ShipmentEntity> shipment = this.ShipmentRepository.findById(shipmentId);

            if (shipment.isEmpty())
            {
                ErrorMessage = "La orden de envio seleccionada no se encuentra registrada en el sistema";
                return ApiResponseDTO.error(ErrorMessage);
            }

            Optional<OrderEntity> order = this.OrderRepository.findById(shipment.get().getOrder().getId());

            if(order.isEmpty())
            {
                ErrorMessage = "La orden asociada al envio no se encuentra registrada en el sistema";
                return ApiResponseDTO.error(ErrorMessage);
            }

            OrderEntity orderUpdate = order.get();
            orderUpdate.setState(OrderState.PENDING);
            this.OrderRepository.save(orderUpdate);

            ShipmentEntity shipmentUpdate = shipment.get();
            shipmentUpdate.setState(ShipmentState.CANCEL);
            this.ShipmentRepository.save(shipmentUpdate);

            return ApiResponseDTO.success("Se ha cancelado con exito la orden de entrega", shipmentUpdate.getId());

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
    public ApiResponseDTO<Integer> CompletedShipment(Integer shipmentId)
    {
        try
        {
            String ErrorMessage = "";
            Optional<ShipmentEntity> shipment = this.ShipmentRepository.findById(shipmentId);

            if (shipment.isEmpty())
            {
                ErrorMessage = "La orden de entrega no ha sido registrada en el sistema";
                return ApiResponseDTO.error(ErrorMessage);
            }

            Optional<OrderEntity> order = this.OrderRepository.findById(shipment.get().getOrder().getId());

            if (order.isEmpty())
            {
                ErrorMessage = "La orden asociada a la entrega no ha sido registrada en el sistema";
                return ApiResponseDTO.error(ErrorMessage);
            }

            int userId = this.GetByUserId();
            Optional<UserEntity> user = this.userRepository.findById(userId);

            if (user.isEmpty())
            {
                ErrorMessage = "El usuario asociado a la entrega como domiciliario no ha sido registrado en el sistema";
                return ApiResponseDTO.error(ErrorMessage);
            }

            OrderEntity orderUpdate = order.get();
            orderUpdate.setState(OrderState.COMPLETED);
            this.OrderRepository.save(orderUpdate);

            ShipmentEntity shipmentUpdate = shipment.get();
            shipmentUpdate.setState(ShipmentState.COMPLETED);
            shipmentUpdate.setDeliveryDate(LocalDateTime.now());
            this.ShipmentRepository.save(shipmentUpdate);

            PaymentEntity payment = new PaymentEntity();
            payment.setState(false);
            payment.setPayValue(7500L);
            payment.setShipment(shipment.get());
            payment.setUser(user.get());
            this.PaymentRepository.save(payment);

            return ApiResponseDTO.success("Se ha actualizado con exito el estado de la orden de envio a entregado", shipmentUpdate.getId());

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

    public ApiResponseDTO<List<ReportTotalsDTO>> GetTotalsDeliveriesForUser()
    {
        try
        {
            List<ReportTotalsDTO> Totals = this.ShipmentRepository.SearchTotalDeliveriesUser(PageRequest.of(0, 5), ShipmentState.COMPLETED);
            return ApiResponseDTO.success("Se ha obtenido con exito los total de pedidos entregados por usuario",Totals);
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

    public int GetByUserId()
    {
        UserEntity user = (UserEntity) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return user.getId();
    }
}
