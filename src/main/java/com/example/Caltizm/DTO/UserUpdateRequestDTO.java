package com.example.Caltizm.DTO;

import lombok.Data;

@Data
public class UserUpdateRequestDTO {

    private String userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String birthYear;
    private String birthMonth;
    private String birthDay;
    private String pccc;

}
