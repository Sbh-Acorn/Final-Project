package com.example.Caltizm.Repository;

import com.example.Caltizm.DTO.AddressResponseDTO;
import com.example.Caltizm.DTO.LoginRequestDTO;
import com.example.Caltizm.DTO.MyPageResponseDTO;
import com.example.Caltizm.DTO.UserUpdateRequestDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    SqlSession session;

    public LoginRequestDTO selectUserLogin(String email){

        return session.selectOne("a.selectUserLogin", email);

    }

    public MyPageResponseDTO selectUserInfo(String email){

        return session.selectOne("a.selectUserInfo", email);

    }

    public int updateUserInfo(UserUpdateRequestDTO userUpdateRequestDTO){

        return session.update("a.updateUserInfo", userUpdateRequestDTO);

    }

    public List<AddressResponseDTO> selectAddressAll(String email){

        return session.selectList("b.selectAddressAll", email);

    }

}
