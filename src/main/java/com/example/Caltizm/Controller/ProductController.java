package com.example.Caltizm.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {
    @GetMapping("/product2")
    public String product2(Model model) {
        return "product/product-list";
    }
}
