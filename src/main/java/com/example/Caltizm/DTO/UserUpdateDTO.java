package com.example.Caltizm.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserUpdateDTO {

    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String birthDate;
    private String pccc;

}
