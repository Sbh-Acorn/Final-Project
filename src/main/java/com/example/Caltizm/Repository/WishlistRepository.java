package com.example.Caltizm.Repository;

import com.example.Caltizm.DTO.WishlistAddDTO;
import com.example.Caltizm.DTO.WishlistDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WishlistRepository {

    @Autowired
    SqlSession session;

    String namespace = "wishlist.";

    public int insertWishlist(WishlistAddDTO wishlistAddDTO){

        return session.insert(namespace + "insertWishlist", wishlistAddDTO);

    }

    public List<WishlistDTO> selectWishlist(String email){

        return session.selectList(namespace + "selectWishlist", email);

    }

    public int deleteWishlist(String wishlistId){

        return session.delete(namespace + "deleteWishlist", wishlistId);

    }

    public int isInWishlist(WishlistAddDTO wishlistAddDTO){

        return session.selectOne(namespace + "isInWishlist", wishlistAddDTO);

    }

}
