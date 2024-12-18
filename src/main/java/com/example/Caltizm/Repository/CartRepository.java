package com.example.Caltizm.Repository;

import com.example.Caltizm.DTO.CartDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CartRepository {

    @Autowired
    SqlSession session;

    public Integer CountCartListByUserId (Integer user_id){
        return session.selectOne("cart.countCartListByUserId" , user_id);
    }

    public void deleteCartListByUserId (Integer user_id) {
        session.delete("cart.deleteCartByUserId", user_id);
    }

    public void insertSessionCartList(Map<String, Object> input){
        session.insert("cart.insertSessionCartList", input);
    }

    public List<CartDTO> selectCartListByUserId (Integer user_id){
        return session.selectList("cart.selectCartListByUserId", user_id);
    }
    public void updateCartListByUserIdAndProductId (Map<String, Object> input){
        session.update("cart.updateAddQuantity", input);
    }
    public void insertCartList (Map<String, Object> input) {
        session.insert("cart.insertCartList" , input);
    }
}
