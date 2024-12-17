package com.example.Caltizm.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @GetMapping("/main")
    public String mainPage(Model model, HttpSession session) {
        // 배너 이미지 리스트 추가
        List<String> bannerImages = List.of(
                "/bannerImages/Coupon-35-im.jpg",
                "/bannerImages/blackbuy.webp",
                "/bannerImages/3.webp",
                "/bannerImages/4.webp"
        );
        model.addAttribute("BannerImages", bannerImages);

        // 세션에서 email 값 가져오기
        String email = (String) session.getAttribute("email");
        model.addAttribute("sessionEmail", email);

        return "main/main";
    }
}