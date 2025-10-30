package com.fulldevcode.ecommerce.backend.infraestructure.DTO;

import com.fulldevcode.ecommerce.backend.infraestructure.models.UserType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
    private Integer id;
    private String username;
    private String lastname;
    private String firstname;
    private String email;
    private String address;
    private String cellphone;
    private String password;
    private String userType;
    private int typeUser;

    public UserDTO(Integer id, String username, String lastname, String firstname, String email, String address, String cellphone, UserType userType){
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.address = address;
        this.cellphone = cellphone;
        this.userType = userType.toString();
    }

}
