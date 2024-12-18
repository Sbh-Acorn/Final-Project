package com.example.Caltizm.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO {
    String last_name; // 성
    String first_name; // 이름
    String email; // 이메일
    String phone_number; // 전화번호
    String birth_date; // 생일
    String password; // 비밀번호
    String pccc; // 통관번호 (선택사항)
}
