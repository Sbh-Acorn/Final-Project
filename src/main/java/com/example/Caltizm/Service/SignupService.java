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

    //이메일 중복확인
    public boolean userCheck(String email) {
        String user = repository.userCheck(email);
        if(user != null) {
            return true; // 중복된 이메일이 존재하면 true
        }
        return false; // 중복된 이메일이 존재하지 않으면 false
    }

    //전화번호 중복확인
    public boolean telDupCheck(String tel) {
        List<String> telList = repository.selectTel();

        // telList에 tel이 포함되어 있는지 확인
        if (telList.contains(tel)) {
            return false; // 동일한 전화번호가 있으면 false 반환
        }
        return true; // 동일한 전화번호가 없으면 true 반환
    }

    //전화번호 010으로 시작하는지 확인
    public boolean telEffCheck(String tel) {
        return tel.startsWith("010"); // "010"으로 시작하면 true 반환
    }

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
