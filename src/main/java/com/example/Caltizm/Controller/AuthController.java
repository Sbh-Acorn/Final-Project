package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.CartDTO;
import com.example.Caltizm.DTO.LoginRequestDTO;
import com.example.Caltizm.Repository.CartRepository;
import com.example.Caltizm.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CartRepository cartRepository;

    @GetMapping("/login")
    public String login1() {

        return "auth/login";

    }

    @PostMapping("/login")
    public String login2(@ModelAttribute LoginRequestDTO loginRequestDTO, HttpSession session) {

        String email = loginRequestDTO.getEmail();
        String password = loginRequestDTO.getPassword();

        System.out.println(email);
        System.out.println(password);
        System.out.println(loginRequestDTO);

        LoginRequestDTO user = userRepository.selectUserLogin(email);
        if (user == null || !password.equals(user.getPassword())) {
            System.out.println("이메일, 비밀번호 불일치");
            return "redirect:/login";
        }

        session.setAttribute("email", user.getEmail());


        // 세션에서 cartList 가져오기
        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");

        System.out.println("이메일 : " + user.getEmail());
        Integer userID = userRepository.selectUserIdByEmail(user.getEmail());
        System.out.println("user_id : " + userID);

        if (cartList != null && !cartList.isEmpty()){
            cartRepository.deleteCartListByUserId(userID);
            Map<String, Object> input = new HashMap<>();
            input.put("user_id", userID);
            input.put("cartList", cartList);
            cartRepository.insertSessionCartList(input);
        }else{
            List<CartDTO> cartListByUserId =  cartRepository.selectCartListByUserId(userID);
            if(cartListByUserId != null){
                cartList = cartListByUserId;
            }
        }


        session.setAttribute("cartList", cartList);
        //모델에 테이블 값 심기

        return "redirect:/main";

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        String email = (String) session.getAttribute("email");
        Integer userID = userRepository.selectUserIdByEmail(email);
        if (userID != null) {
            List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");

            if (cartList != null) {
                Map<String, Object> input = new HashMap<>();
                input.put("user_id", userID);
                input.put("cartList", cartList);
                cartRepository.insertSessionCartList(input);
            }
            session.invalidate();
            return "redirect:/main";
        }


        return "redirect:/main";

    }

}
