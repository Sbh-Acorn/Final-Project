package com.example.Caltizm.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

        public String brand;
        public String product_id;
        public String image_url;
        public String name;
        public Double original_price; // null 허용
        public double current_price;
        public String description;
        public String category1;
        public String category2;
        public String category3;
        public boolean is_excludedVoucher;


}
