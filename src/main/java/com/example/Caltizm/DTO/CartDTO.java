package com.example.Caltizm.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    private String product_id;
    private String productName;
    private double price;
    private int quantity = 1;  // 기본값을 1로 설정

    public CartDTO(String product_id, String productName, double price) {
        this.product_id = product_id;
        this.productName = productName;
        this.price = price;
    }
}
