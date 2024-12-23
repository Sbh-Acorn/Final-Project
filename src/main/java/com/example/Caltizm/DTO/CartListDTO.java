package com.example.Caltizm.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartListDTO {

    public String brand;
    public String product_id;
    public String image_url;
    public String name;
    public Double original_price; // null 허용
    public double current_price;
    public String viewOriginal_price;
    public String viewCurrent_price;
    public boolean is_excludedVoucher;
    public boolean is_fta;
    public int quantity;


    public CartListDTO(CartDTO cartDTO , ProductDTO productDTO){
        this.brand = productDTO.getBrand();
        this.product_id = productDTO.getProduct_id();
        this.image_url = productDTO.getImage_url();
        this.name = productDTO.getName();
        this.original_price = productDTO.getOriginal_price();
        this.current_price = productDTO.getCurrent_price();
        this.is_excludedVoucher = productDTO.is_excludedVoucher();
        this.is_fta = productDTO.is_fta();
        this.quantity = cartDTO.getQuantity();
    }
}
