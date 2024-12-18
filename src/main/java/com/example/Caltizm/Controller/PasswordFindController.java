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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
    public String sendPw(@RequestParam(value = "email") String email, RedirectAttributes redirectAttributes) {
        if(!(mailService.userCheck(email))) {
            redirectAttributes.addFlashAttribute("message", "이메일이 존재하지 않습니다.");
            System.out.println("이메일 없음");
            return "redirect:/login";
        }

        SignupRequestDTO user = signupService.selectUser(email);

        String password = user.getPassword();
        try {
            System.out.println("이메일 전송 완료");
            redirectAttributes.addFlashAttribute("message", "비밀번호가 이메일로 전송되었습니다.");
            mailService.sendEmail(email,password);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/login";  // 로그인 페이지로 리다이렉트
    }
}
