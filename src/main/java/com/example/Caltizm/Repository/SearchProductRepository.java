package com.example.Caltizm.Repository;

import com.example.Caltizm.DTO.ProductDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class SearchProductRepository {

    @Autowired
    private SqlSession sqlSession;

    // 이름으로 ID와 이름 검색
    public List<Map<String, Object>> findProductsByName(String query) {
        return sqlSession.selectList("com.example.Caltizm.Repository.SearchProductRepository.findProductsByName", query);
    }

    // ID로 상품 전체 데이터 검색
    public ProductDTO findProductById(String id) {
        return sqlSession.selectOne("com.example.Caltizm.Repository.SearchProductRepository.findProductById", id);
    }
}
