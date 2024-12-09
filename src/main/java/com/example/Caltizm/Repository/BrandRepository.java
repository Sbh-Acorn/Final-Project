package com.example.Caltizm.Repository;


import com.example.Caltizm.DTO.BrandDTO;
import com.example.Caltizm.Service.GetBrandDataService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Repository
public class BrandRepository {

    @Autowired
    GetBrandDataService service;

    @Autowired
    SqlSession session;

    public Set<BrandDTO> collectAndInsertBrandData() throws IOException {
        Set<BrandDTO> brands = service.collectBrand();
        // 안전 업데이트 모드 끄기
        session.update("brand.setSafeUpdateOff");

        // 1. 모든 데이터의 삭제여부를 true로
        session.update("brand.setDeletedTrue");

        for (BrandDTO brand : brands) {
            try {
                // 2. 동일한 이름의 데이터 존재 여부 확인
                int checkName = session.selectOne("brand.checkName" , brand.getName());
                if(checkName > 0){
                    session.update("brand.setDeletedFalse", brand.getName());
                }else {
                    session.insert("brand.insert" , brand);
                    System.out.println("새로운 브랜드 : " + brand.getName());
                }

            } catch (Exception e) { // 예외 처리
                System.err.println("Failed to process: " + brand);
                e.printStackTrace();
            }
        }

        // 안전 업데이트 모드 다시 켜기
        session.update("brand.setSafeUpdateOn");
        return brands;
    }

    public List<BrandDTO> getAllBrand(){
       List<BrandDTO> brands = session.selectList("brand.selectAll");
       return brands;

    }
    public BrandDTO getBrandByName (String name){
        BrandDTO brand = session.selectOne("brand.selectOne" , name);
        return brand;
    }

}




