package com.example.Caltizm.Repository;

import com.example.Caltizm.DTO.SignupRequestDTO;
import com.example.Caltizm.DTO.UserAddressDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SignupRepository {

    String namespace = "singup";

    @Autowired
    SqlSession session;

    //모든 유저 정보
    public List<SignupRequestDTO> selectAllUser() {
        return session.selectList(namespace + ".selectAllUser");
    }
    
    //유저 한명 검색
    public SignupRequestDTO selectUser(String email) {
        return session.selectOne(namespace + ".selectUser", email);
    }

    //유저 ID 검색
    public int searchUserID(String email) {
        return session.selectOne(namespace + ".searchUserID", email);
    }

    //유저 등록
    public int registUser(SignupRequestDTO user) {
        if (user.getPccc() == null) {
            return session.insert(namespace + ".registerUser2", user);
        } else {
            return session.insert(namespace + ".registerUser1", user);
        }
    }

    //유저 주소 등록
    public  int registUserAddr(UserAddressDTO address) {
        return session.insert(namespace + ".registerUserAddr",address);
    }
}
