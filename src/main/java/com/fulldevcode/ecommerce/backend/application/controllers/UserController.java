package com.fulldevcode.ecommerce.backend.application.controllers;

import com.fulldevcode.ecommerce.backend.domain.Services.UserService;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.ApiResponseDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.LoginDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.UserDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.DTO.UserDetail;
import com.fulldevcode.ecommerce.backend.infraestructure.models.UserEntity;
import com.fulldevcode.ecommerce.backend.infraestructure.security.JwtUtil;
import org.apache.catalina.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public  UserController(UserService userServi, JwtUtil utilJwt)
    {
        this.userService = userServi;
        this.jwtUtil = utilJwt;
    }

    @GetMapping()
    public ApiResponseDTO<List<UserDetail>> GetAllAdminUser()
    {
        ApiResponseDTO<List<UserDetail>> response = userService.IndexAdminUsers();
        return  response;
    }

    @GetMapping("/userInfo/{id}")
    public ApiResponseDTO<UserDetail> GetByIdUser(@PathVariable Integer id)
    {
        ApiResponseDTO<UserDetail> response = userService.FindByIdUser(id);
        return response;
    }

    @PostMapping("/register")
    public ApiResponseDTO<UserDTO> CreateUser (@RequestBody UserDTO user)
    {
        ApiResponseDTO<UserDTO> response = userService.Create(user);
        return response;
    }

    @PutMapping("editUser/{id}")
    public ApiResponseDTO<UserDTO> UpdateUser (@PathVariable Integer id, @RequestBody UserDTO user)
    {
        ApiResponseDTO<UserDTO> response = userService.Edit(id, user);
        return response;
    }

    @DeleteMapping("/{id}")
    public ApiResponseDTO<UserDetail> DeleteUser (@PathVariable Integer id)
    {
        ApiResponseDTO<UserDetail> response = userService.Delete(id);
        return response;
    }

    @PostMapping("/login")
    public ApiResponseDTO<String> Login (@RequestBody LoginDTO login) {

        ApiResponseDTO<UserEntity> UserLoginResponse = userService.Login(login);

        if (UserLoginResponse.isSuccess() == true)
        {
            UserEntity User = UserLoginResponse.getData();
            String token = jwtUtil.GenerateToken(User.getEmail(), User.getUserType().toString(), User.getId());
            return ApiResponseDTO.success(UserLoginResponse.getMessage(), token);

        } else
        {
            return  ApiResponseDTO.error(UserLoginResponse.getMessage());
        }
    }
}
