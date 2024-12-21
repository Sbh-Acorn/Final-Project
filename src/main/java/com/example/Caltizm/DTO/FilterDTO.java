package com.example.Caltizm.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDTO {

    private List<String> brands;
    private List<String> categories;
    private PriceRange price;
    private String tax;
    private String fta;


    @Data
    public static class PriceRange {
        private double min;
        private double max;


    }
}
