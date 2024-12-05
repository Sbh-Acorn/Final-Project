package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.LoginRequestDTO;
import com.example.Caltizm.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    UserRepository repository;

    @GetMapping("/main")
    public String main(){

        return "main/main";

    }

    @GetMapping("/login")
    public String login1(){

        return "auth/login";

    }

    @PostMapping("/login")
    public String login2(@ModelAttribute LoginRequestDTO loginRequestDTO, HttpSession session){

        String userId = loginRequestDTO.getUserId();
        String password = loginRequestDTO.getPassword();

        System.out.println(userId);
        System.out.println(password);
        System.out.println(loginRequestDTO);

        LoginRequestDTO user = repository.selectUserLogin(userId);
        if(user == null || !password.equals(user.getPassword())){
            System.out.println("아이디, 비밀번호 불일치");
            return "redirect:/login";
        }

        session.setAttribute("userId", user.getUserId());

        return "redirect:/main";

    }

    @GetMapping("/logout")
    public String logout(HttpSession session){

        session.invalidate();

        return "redirect:/main";

    }

}
