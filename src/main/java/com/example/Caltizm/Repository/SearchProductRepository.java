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

    // 이름으로 ID와 이름 검색 (10개 제한)
    public List<Map<String, Object>> findProductsByName(String query) {
        return sqlSession.selectList("com.example.Caltizm.Repository.SearchProductRepository.findProductsByName", query);
    }

    // 이름으로 ID와 이름 검색 (전체 데이터)
    public List<String> findAllProductIdsByName(String query) {
        return sqlSession.selectList("com.example.Caltizm.Repository.SearchProductRepository.findAllProductIdsByName", query);
    }
    // ID로 상품 전체 데이터 검색
    public ProductDTO findProductById(String id) {
        return sqlSession.selectOne("com.example.Caltizm.Repository.SearchProductRepository.findProductById", id);
    }

    // 검색된 상품 ID 리스트로 데이터를 가져오기
    public List<ProductDTO> findProductsByIds(List<String> productIds) {
        return sqlSession.selectList("com.example.Caltizm.Repository.SearchProductRepository.findProductsByIds", productIds);
    }

    // 브랜드 목록 조회
    public List<String> findBrandsByProductIds(List<String> productIds) {
        return sqlSession.selectList("com.example.Caltizm.Repository.SearchProductRepository.findBrandsByProductIds", productIds);
    }

    // 카테고리 목록 조회
    public List<String> findCategoriesByProductIds(List<String> productIds) {
        return sqlSession.selectList("com.example.Caltizm.Repository.SearchProductRepository.findCategoriesByProductIds", productIds);
    }

    // 최대 가격 조회
    public Map<String, Object> findMaxPriceByProductIds(List<String> productIds) {
        return sqlSession.selectOne("com.example.Caltizm.Repository.SearchProductRepository.findMaxPriceByProductIds", productIds);
    }
}
