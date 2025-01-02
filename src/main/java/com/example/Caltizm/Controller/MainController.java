package com.example.Caltizm.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class MainController {
    @GetMapping("/")
    public String redirectToMain() {
        return "redirect:/main";
    }
    private static final String BANNER_IMAGE_PATH = new File("src/main/resources/static/bannerImages").getAbsolutePath();

    @GetMapping("/main")
    public String mainPage(Model model, HttpSession session) {
        // 배너 이미지 리스트 자동 생성
        List<String> bannerImages = getBannerImagePaths();
        model.addAttribute("BannerImages", bannerImages);
        // 세션에서 email 값 가져오기
        String email = (String) session.getAttribute("email");
        model.addAttribute("sessionEmail", email);

        return "main/main";
    }

    private List<String> getBannerImagePaths() {
        List<String> imagePaths = new ArrayList<>();
        File directory = new File(BANNER_IMAGE_PATH);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".webp"));
            if (files != null) {
                Arrays.sort(files); // 파일명 기준 정렬
                for (File file : files) {
                    imagePaths.add("/bannerImages/" + file.getName());
                }
            }
        }
        return imagePaths;
    }
}