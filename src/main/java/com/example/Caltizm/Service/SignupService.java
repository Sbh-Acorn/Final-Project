package com.example.Caltizm.Service;

import com.example.Caltizm.DTO.SignupRequestDTO;
import com.example.Caltizm.DTO.UserAddressDTO;
import com.example.Caltizm.Repository.SignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SignupService {

    @Autowired
    SignupRepository repository;

    //모든 유저 정보
    public List<SignupRequestDTO> selectAllUser() {
        return repository.selectAllUser();
    }

    //유저 한명 검색
    public SignupRequestDTO selectUser(String email) {
        return repository.selectUser(email);
    }

    //유저 ID 검색
    public int searchUserID(String email) {
        return repository.searchUserID(email);
    }

    //유저 등록
    public int registUser(SignupRequestDTO user) {
        return repository.registUser(user);
    }

    //유저 주소 등록
    public  int registUserAddr(UserAddressDTO address, String email) {
        int user_id = searchUserID(email);
        address.setUser_id(String.valueOf(user_id));
        return repository.registUserAddr(address);
    }

}
