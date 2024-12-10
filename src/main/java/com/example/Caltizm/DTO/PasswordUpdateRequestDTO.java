package com.example.Caltizm.DTO;

import lombok.Data;

@Data
public class PasswordUpdateRequestDTO {

    private String email;
    private String newPassword;

}
