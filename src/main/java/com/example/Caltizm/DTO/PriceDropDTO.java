package com.example.Caltizm.DTO;

import lombok.Data;

@Data
public class PriceDropDTO {

    private String productId;
    private double previousPrice;
    private double currentPrice;

}
