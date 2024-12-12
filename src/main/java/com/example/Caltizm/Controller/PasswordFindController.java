package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.SignupRequestDTO;
import com.example.Caltizm.Service.MailService;
import com.example.Caltizm.Service.SignupService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PasswordFindController {

    @Autowired
    private MailService mailService;

    @Autowired
    private SignupService signupService;

    @GetMapping("/find-password")
    public String findPassword() {
        return "findPw/find-password";
    }

    // 비밀번호 찾기
    @PostMapping("/find-password")
    public String sendPw(@RequestParam("email") String email) {

        SignupRequestDTO user = signupService.selectUser(email);

        String password = user.getPassword();
        try {
            mailService.sendEmail(email,password);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/login";  // 로그인 페이지로 리다이렉트
    }
}
