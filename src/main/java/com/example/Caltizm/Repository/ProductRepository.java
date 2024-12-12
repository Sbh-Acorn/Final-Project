package com.example.Caltizm.Repository;

import com.example.Caltizm.DTO.CartDTO;
import com.example.Caltizm.DTO.CategoryDTO;
import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Service.GetFTADataService;
import com.example.Caltizm.Service.GetProductDataService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ProductRepository {

    @Autowired
    GetProductDataService service;

    @Autowired
    GetFTADataService FTAservice;

    @Autowired
    SqlSession session;


    public void collectAndInsertProductData() {
        Set<ProductDTO> products = service.collectProductDetailsAsync();
        Set<CategoryDTO> category1 = new HashSet<>();
        for (ProductDTO product : products) {
            CategoryDTO category = new CategoryDTO();
            if (product.getCategory1() != null) {
                category.setCategory1(product.getCategory1());
            }
            category1.add(category);
        }

        for (CategoryDTO category : category1) {
            int checkName = session.selectOne("category.checkName1", category.getCategory1());
            if (checkName < 1) {
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

        for (CategoryDTO category : category2) {
            int checkName = session.selectOne("category.checkName2", category.getCategory2());
            if (checkName < 1) {
                int category1ID = session.selectOne("category.category1ID", category.getCategory1());
                if (category1ID > 0) {
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

        for (CategoryDTO category : category3) {
            if (category.getCategory3() != null) {
                int checkName = session.selectOne("category.checkName3", category.getCategory3());
                if (checkName < 1) {

                    int category2ID = session.selectOne("category.category2ID", category.getCategory2());

                    if (category2ID > 0) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("category3", category.getCategory3());
                        params.put("category2Id", category2ID);
                        session.insert("category.insert3", params);
                    }
                }
            }

        }
        // 안전 업데이트 모드 끄기
        session.update("product.setSafeUpdateOff");

        // 1. 모든 데이터의 삭제여부를 true로
        session.update("product.setDeletedTrue");

        //상품 불러오기
        for (ProductDTO product : products) {
            //동일한 상품이 있는지?
            int checkId = session.selectOne("product.checkId", product.getProduct_id());
            if (checkId > 0) {
                //동일한 이름이 테이블에 존재하면 삭제여부를 false로
                session.update("product.setDeletedFalse", product.getProduct_id());
            } else {
                //아이디 찾기
                Map<String, Object> params = new HashMap<>();
                int brand_id = session.selectOne("product.brandId", product.getBrand());
                int category1_id = session.selectOne("product.category1Id", product.getCategory1());
                int category2_id = session.selectOne("product.category2Id", product.getCategory2());
                if (product.getCategory3() != null) {
                    int category3_id = session.selectOne("product.category3Id", product.getCategory3());
                    params.put("category3_id", category3_id);
                }
                params.put("product_id", product.getProduct_id());
                params.put("brand_id", brand_id);
                params.put("image_url", product.getImage_url());
                params.put("name", product.getName());
                params.put("original_price", product.getOriginal_price());
                params.put("current_price", product.getCurrent_price());
                params.put("description", product.getDescription());
                params.put("category1_id", category1_id);
                params.put("category2_id", category2_id);
                params.put("is_excludedVoucher", product.is_excludedVoucher());
                session.insert("product.insert", params);
            }


        }
        // 안전 업데이트 모드 다시 켜기
        session.update("brand.setSafeUpdateOn");

        //FTA 가능 여부
        Set<String> FTA = FTAservice.collectFTAItemCode();
        for (String product_id : FTA) {
            session.update("product.setFTA", product_id);
        }
        System.out.println("수집 및 DB 저장 완료");
    }

    public List<ProductDTO> getProduct() {
        List<ProductDTO> products = session.selectList("product.selectAll");
        return products;

    }

    public ProductDTO getBrandByName(String name) {
        ProductDTO product = session.selectOne("product.selectOne", name);
        return product;
    }


    public CartDTO getCartItemInfo(String product_id) {
        CartDTO cartItemInfo = session.selectOne("product.selectCartItemInfo", product_id);
        return cartItemInfo;
    }


}

