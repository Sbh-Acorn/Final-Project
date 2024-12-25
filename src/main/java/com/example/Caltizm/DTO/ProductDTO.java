package com.example.Caltizm.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

        public String brand;
        public String product_id;
        public String image_url;
        public String name;
        @Setter
        public Double original_price; // null 허용
        @Setter
        public double current_price;
        public String description;
        public String category1;
        public String category2;
        public String category3;
        public boolean is_excludedVoucher;
        public boolean is_fta;


        public ProductDTO(String brand, String product_id, String image_url, String name,
                          Double original_price, double current_price, String description,
                          String category1, String category2, String category3,
                          boolean is_excludedVoucher) {
                this.brand = brand;
                this.product_id = product_id;
                this.image_url = image_url;
                this.name = name;
                this.original_price = original_price;
                this.current_price = current_price;
                this.description = description;
                this.category1 = category1;
                this.category2 = category2;
                this.category3 = category3;
                this.is_excludedVoucher = is_excludedVoucher;
                this.is_fta = false;
        }

}


