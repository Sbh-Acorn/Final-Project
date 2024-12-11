package com.example.Caltizm.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {

    public String category1;
    public String category2;
    public String category3;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class category1{
        String name;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class category2{
        String name;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class category3{
        String name;
    }

}
