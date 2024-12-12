package com.example.Caltizm.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    private String productName;
    private double price;
    private int quantity = 1;  // 기본값을 1로 설정

    public CartDTO(String productName, double price) {
        this.productName = productName;
        this.price = price;
    }
}
