package com.example.Caltizm.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;

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
        public boolean is_fta;
        public String viewOriginal_price;
        public String viewCurrent_price;

        private static final DecimalFormat formatter = new DecimalFormat("#,###");

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

                // 숫자 포맷팅하여 문자열로 저장
                this.viewOriginal_price = (original_price != null) ? formatter.format(original_price) : null;
                this.viewCurrent_price = formatter.format(current_price);
        }

        // Optional: 세터 또는 메서드를 통해 가격을 포맷팅하여 추가할 수 있음
        public void setOriginal_price(Double original_price) {
                this.original_price = original_price;
                this.viewOriginal_price = (original_price != null) ? formatter.format(original_price) : null;
        }

        public void setCurrent_price(double current_price) {
                this.current_price = current_price;
                this.viewCurrent_price = formatter.format(current_price);
        }
}


