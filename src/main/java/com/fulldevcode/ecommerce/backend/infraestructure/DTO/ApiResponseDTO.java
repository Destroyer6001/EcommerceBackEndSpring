package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponseDTO<T>
{
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponseDTO()
    {
        this.timestamp = LocalDateTime.now();
    }

    public  ApiResponseDTO(boolean Success, String Message, T data)
    {
        this.success = Success;
        this.message = Message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return new ApiResponseDTO<>(true, message, data);
    }

    public static <T> ApiResponseDTO<T> error(String message) {
        return new ApiResponseDTO<>(false, message, null);
    }

}
