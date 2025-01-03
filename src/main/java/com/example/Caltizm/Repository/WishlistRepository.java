package com.example.Caltizm.Repository;

import com.example.Caltizm.DTO.NotificationDTO;
import com.example.Caltizm.DTO.WishlistRequestDTO;
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

    public int insertWishlist(WishlistRequestDTO wishlistRequestDTO){

        return session.insert(namespace + "insertWishlist", wishlistRequestDTO);

    }

    public List<WishlistDTO> selectWishlist(String email){

        return session.selectList(namespace + "selectWishlist", email);

    }

    public int deleteWishlist(WishlistRequestDTO wishlistRequestDTO){

        return session.delete(namespace + "deleteWishlist", wishlistRequestDTO);

    }

    public boolean isInWishlist(WishlistRequestDTO wishlistRequestDTO){

        int result = session.selectOne(namespace + "isInWishlist", wishlistRequestDTO);
        return result > 0;

    }

    public int insertNotification(){

        return session.insert(namespace + "insertNotification");

    }

    public int updateNotificationSent(){

        return session.update(namespace + "updateNotificationSent");

    }

    public void setSafeUpdateOff(){
        session.update(namespace + "setSafeUpdateOff");
    }

    public void setSafeUpdateOn(){
        session.update(namespace + "setSafeUpdateOn");
    }

    public List<NotificationDTO> selectNotification(String email){

        return session.selectList(namespace + "selectNotification", email);

    }

    public int readNotification(String notificationId){

        return session.update(namespace + "readNotification", notificationId);

    }

    public int selectWishlistSize(String email){

        return session.selectOne(namespace + "selectWishlistSize", email);

    }

}
