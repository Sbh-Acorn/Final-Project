package com.example.Caltizm.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressDTO {

    private String user_id;
    private String address;
    private String detail;
    private String zip_code;


    public UserAddressDTO(String address, String detail, String zip_code) {
        this.address = address;
        this.detail = detail;
        this.zip_code = zip_code;
    }
}
