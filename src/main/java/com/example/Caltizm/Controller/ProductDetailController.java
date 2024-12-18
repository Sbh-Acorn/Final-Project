package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Repository.SearchProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductDetailController {

    @Autowired
    private SearchProductRepository searchProductRepository;

    @GetMapping("/productDetail")
    public String getProductDetail(@RequestParam("name") String name, Model model) {
        ProductDTO product = searchProductRepository.findProductsByName(name)
                .stream()
                .findFirst()
                .orElse(null);

        if (product == null) {
            return "error/404";
        }

        model.addAttribute("product", product);
        return "product/productDetail";
    }
}
