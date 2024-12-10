package com.example.Caltizm.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

        public String brand;
        public String itemCode;
        public String name;
        public String currentPrice;
        public String originalPrice;
        public String detail;
        public String category1;
        public String category2;
        public String category3;
        public String imgURL;
        public boolean FTA = false;
        public boolean excludedVoucher;

}
