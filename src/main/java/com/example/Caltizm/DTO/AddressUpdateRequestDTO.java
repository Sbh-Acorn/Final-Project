package com.example.Caltizm.DTO;

import lombok.Data;

@Data
public class AddressUpdateRequestDTO {

    private String addressId;
    private String email;
    private String address;
    private String detail;
    private String zipCode;

}
