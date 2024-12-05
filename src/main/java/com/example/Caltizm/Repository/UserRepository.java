package com.example.Caltizm.Repository;

import com.example.Caltizm.DTO.LoginRequestDTO;
import com.example.Caltizm.DTO.MyPageResponseDTO;
import com.example.Caltizm.DTO.UserUpdateRequestDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    @Autowired
    SqlSession session;

    public LoginRequestDTO selectUserLogin(String userId){

        return session.selectOne("a.selectUserLogin", userId);

    }

    public MyPageResponseDTO selectUserInfo(String userId){

        return session.selectOne("a.selectUserInfo", userId);

    }

    public int updateUserInfo(UserUpdateRequestDTO userUpdateRequestDTO){

        return session.update("a.updateUserInfo", userUpdateRequestDTO);

    }

}
