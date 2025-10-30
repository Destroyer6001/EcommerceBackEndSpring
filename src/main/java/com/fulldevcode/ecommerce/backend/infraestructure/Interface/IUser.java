package com.fulldevcode.ecommerce.backend.infraestructure.Interface;

import com.fulldevcode.ecommerce.backend.infraestructure.DTO.UserDTO;
import com.fulldevcode.ecommerce.backend.infraestructure.models.UserEntity;
import com.fulldevcode.ecommerce.backend.infraestructure.models.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IUser extends JpaRepository<UserEntity, Integer> {

    @Query("""
            SELECT us FROM UserEntity us
            WHERE us.email = :email AND us.id != :id
            """)
    Optional<UserEntity> FindByEmailAndId(@Param("id") Integer id, @Param("email") String email);

    Optional<UserEntity> findByEmail(String email);

    @Query("""
            SELECT new com.fulldevcode.ecommerce.backend.infraestructure.DTO.UserDTO(
                us.id,
                us.username,
                us.lastname,
                us.firstname,
                us.email,
                us.address,
                us.cellphone,
                us.userType
            ) FROM UserEntity us
            WHERE us.userType = :rol
            """)
    List<UserDTO> findUsersAdmin (@Param("rol") UserType rol);
}
