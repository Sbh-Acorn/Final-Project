package com.example.Caltizm.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
public class MainController {

    @GetMapping("/main")
    public String mainPage(Model model){
        List<String> bannerImages = List.of(
                "/bannerImages/Coupon-35-im.jpg",
                "/bannerImages/blackbuy.webp",
                "/bannerImages/3.webp",
                "/bannerImages/4.webp"
        );
        model.addAttribute("BannerImages",bannerImages);
        return "main/mainTest";
    }
}
