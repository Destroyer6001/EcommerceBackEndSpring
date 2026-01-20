package com.fulldevcode.ecommerce.backend.domain.Services;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ApiResponseDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.LoginDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.UserDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.UserDetail;
import com.fulldevcode.ecommerce.backend.infraestructure.Interface.IUser;
import com.fulldevcode.ecommerce.backend.infraestructure.models.UserEntity;
import com.fulldevcode.ecommerce.backend.infraestructure.models.UserType;
import jakarta.persistence.PersistenceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final IUser IUserRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserService (IUser UserRepo) {
        this.IUserRepository = UserRepo;
    }

    public ApiResponseDTO<List<UserDetail>> IndexAdminUsers ()
    {
        try
        {
            List<UserDetail> AdminUsers = IUserRepository.findUsersAdmin(UserType.ADMIN);
            return ApiResponseDTO.success("Lista de administradores obtenida con exito", AdminUsers);
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

    public ApiResponseDTO<UserDetail> FindByIdUser(Integer id)
    {
        try
        {
            Optional<UserEntity> UserEntity = IUserRepository.findById(id);

            if (UserEntity.isEmpty())
            {
                return ApiResponseDTO.error("El usuario no esta registrado en el sistema");
            }

            UserDetail user = new UserDetail();
            user.setId(UserEntity.get().getId());
            user.setFirstname(UserEntity.get().getFirstname());
            user.setLastname(UserEntity.get().getLastname());
            user.setEmail(UserEntity.get().getEmail());
            user.setCellphone(UserEntity.get().getCellphone());
            user.setAddress(UserEntity.get().getAddress());
            user.setUsername(UserEntity.get().getUsername());

            return ApiResponseDTO.success("El usuario ha sido obtenido correctamente", user);

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

    public ApiResponseDTO<UserDTO> Create(UserDTO userdto)
    {
        try
        {
            Optional<UserEntity> UserEmail = IUserRepository.findByEmail(userdto.getEmail());

            if (UserEmail.isPresent())
            {
                return  ApiResponseDTO.error("Ya hay un usuario registrado con este correo");
            }

            UserType UserRole = userdto.getTypeUser() == 1 ? UserType.ADMIN : userdto.getTypeUser() == 2 ? UserType.USER : UserType.DELIVERY;
            String password = passwordEncoder.encode(userdto.getPassword());

            UserEntity user = new UserEntity();
            user.setFirstname(userdto.getFirstname());
            user.setLastname(userdto.getLastname());
            user.setUsername(userdto.getUsername());
            user.setEmail(userdto.getEmail());
            user.setCellphone(userdto.getCellphone());
            user.setAddress(userdto.getAddress());
            user.setPassword(password);
            user.setUserType(UserRole);

            UserEntity userResponse = IUserRepository.save(user);

            return ApiResponseDTO.success("Se ha creado con exito el nuevo usuario", userdto);
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

    public  ApiResponseDTO<UserDTO> Edit(Integer id, UserDTO userDTO)
    {
        try
        {
            Optional<UserEntity> UserEmail = IUserRepository.FindByEmailAndId(id, userDTO.getEmail());

            if (UserEmail.isPresent())
            {
                return ApiResponseDTO.error("Ya hay un usuario registrado con este email");
            }

            Optional<UserEntity> UserById = IUserRepository.findById(id);

            if (UserById.isEmpty())
            {
                return ApiResponseDTO.error(("El usuario no se encuentra registrado en el sistema"));
            }

            UserEntity user = UserById.get();

            user.setFirstname(userDTO.getFirstname());
            user.setLastname(userDTO.getLastname());
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setAddress(userDTO.getAddress());
            user.setCellphone(userDTO.getCellphone());

            if (!userDTO.getPassword().isEmpty())
            {
                String password = passwordEncoder.encode(userDTO.getPassword());
                user.setPassword(password);
            }

            IUserRepository.save(user);

            return ApiResponseDTO.success("El usuario ha sido actualizado con exito", userDTO);
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

    public ApiResponseDTO<UserDetail> Delete(Integer id)
    {
        try
        {
            Optional<UserEntity> UserById = IUserRepository.findById(id);

            if (UserById.isEmpty())
            {
                return ApiResponseDTO.error("El usuario no se encuentra registrado en el sistema");
            }

            if (UserById.get().getUserType() == UserType.USER)
            {
                return  ApiResponseDTO.error("No puedes eliminar un usuario con compras asociadas");
            }

            UserEntity user = UserById.get();
            IUserRepository.delete(user);

            UserDetail userDetail = new UserDetail();
            userDetail.setId(user.getId());
            userDetail.setFirstname(user.getFirstname());
            userDetail.setLastname(user.getLastname());
            userDetail.setEmail(user.getEmail());
            userDetail.setCellphone(user.getCellphone());
            userDetail.setAddress(user.getAddress());
            userDetail.setUsername(user.getUsername());

            return ApiResponseDTO.success("El usuario ha sido eliminado con exito", userDetail);

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

    public ApiResponseDTO<UserEntity> Login(LoginDTO loginDTO) {

        try
        {
            Optional<UserEntity> UserEmail = IUserRepository.findByEmail(loginDTO.getEmail());

            if (UserEmail.isEmpty())
            {
                return ApiResponseDTO.error("El usuario no se encuentra registado en el sistema");
            }

            UserEntity User = UserEmail.get();

            if(!passwordEncoder.matches(loginDTO.getPassword(), User.getPassword()))
            {
                return ApiResponseDTO.error("La contraseña ingresada es incorrecta");
            }

            return ApiResponseDTO.success("Usuario logueado con exito", User);

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

    public ApiResponseDTO<List<UserDetail>> GetDeliveryUsers()
    {
        try
        {
            List<UserDetail> userDetails = this.IUserRepository.findUserDelivery(UserType.DELIVERY);
            return ApiResponseDTO.success("Se ha encontrado con exito la lista de domiciliarios", userDetails);
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
