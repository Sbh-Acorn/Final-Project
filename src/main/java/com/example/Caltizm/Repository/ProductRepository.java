package com.example.Caltizm.Repository;

import com.example.Caltizm.DTO.CategoryDTO;
import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Service.GetProductDataService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class ProductRepository {

    @Autowired
    GetProductDataService service;

    @Autowired
    SqlSession session;

    public void collectAndInsertCategoryData(){
        Set<ProductDTO> products = service.collectProductDetailsAsync();
        Set<CategoryDTO> category1 = new HashSet<>();
        for (ProductDTO product : products) {
            CategoryDTO category = new CategoryDTO();
            if (product.getCategory1() != null) {
                category.setCategory1(product.getCategory1());
            }
            category1.add(category);
        }

        for(CategoryDTO category : category1){
            int checkName = session.selectOne("category.checkName1" , category.getCategory1());
            if(checkName < 1){
                session.insert("category.insert1", category.getCategory1());
            }
        }

        Set<CategoryDTO> category2 = new HashSet<>();
        for (ProductDTO product : products) {
            CategoryDTO category = new CategoryDTO();
            if (product.getCategory1() != null) {
                category.setCategory1(product.getCategory1());
            }
            if (product.getCategory2() != null) {
                category.setCategory2(product.getCategory2());
            }
            category2.add(category);
        }

        for(CategoryDTO category : category2){
            int checkName = session.selectOne("category.checkName2" , category.getCategory2());
            if(checkName < 1){
                int category1ID = session.selectOne("category.category1ID", category.getCategory1());
                if(category1ID > 0) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("category2", category.getCategory2());
                    params.put("category1Id", category1ID);
                    session.insert("category.insert2", params);
                }
            }
        }


        Set<CategoryDTO> category3 = new HashSet<>();
        for (ProductDTO product : products) {
            CategoryDTO category = new CategoryDTO();
            if (product.getCategory1() != null) {
                category.setCategory1(product.getCategory1());
            }
            if (product.getCategory2() != null) {
                category.setCategory2(product.getCategory2());
            }
            if (product.getCategory3() != null) {
                category.setCategory3(product.getCategory3());
            }
            category3.add(category);
        }

        for(CategoryDTO category : category3){
            if(category.getCategory3() != null){
                int checkName = session.selectOne("category.checkName3", category.getCategory3());
                if(checkName < 1){

                    int category2ID = session.selectOne("category.category2ID", category.getCategory2());

                    if( category2ID > 0) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("category3", category.getCategory3());
                        params.put("category2Id", category2ID);
                        session.insert("category.insert3", params);
                    }
                }
            }

        }

    }

    public static void main(String[] args) {
        ProductRepository repository = new ProductRepository();
        repository.collectAndInsertCategoryData();
    }
}
