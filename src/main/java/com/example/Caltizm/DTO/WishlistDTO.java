package com.example.Caltizm.DTO;

import lombok.Data;

@Data
public class WishlistDTO {

    private String wishlistId;
    private String productId;
    private String name;
    private String brandName;
    private String imageUrl;
    private Double originalPrice;
    private double currentPrice;

}
