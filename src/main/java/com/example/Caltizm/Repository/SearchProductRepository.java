package com.example.Caltizm.Repository;

import com.example.Caltizm.DTO.ProductDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SearchProductRepository {

    @Autowired
    private SqlSession sqlSession;

    public List<ProductDTO> findProductsByName(String query) {
        return sqlSession.selectList("com.example.Caltizm.Repository.SearchProductRepository.findProductsByName", query);

    }
}
