package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Comparator;
import java.util.List;

@Controller
public class ProductController_Test {

    @Autowired
    ProductRepository repository;

    // 모든 메서드에서 사용할 제품 리스트를 미리 로드
    @ModelAttribute("products")
    public List<ProductDTO> getAllProducts() {
        List<ProductDTO> products = repository.getProduct();
        products.sort(Comparator.comparing(ProductDTO::getBrand));
        return products;
    }

    @GetMapping("/product")
    public String product(Model model) {
        return "product/product-list_test";  // products는 이미 Model에 추가됨
    }

    @GetMapping("/product/{product_id}")
    public String productDetail(@PathVariable(name = "product_id") String product_id, Model model) {
        // Model에서 products를 가져오고 List로 캐스팅
        List<ProductDTO> products = (List<ProductDTO>) model.getAttribute("products");

        // itemCode에 해당하는 Product 찾기
        ProductDTO product = products.stream()
                .filter(p -> p.getProduct_id().equals(product_id))
                .findFirst()
                .orElse(null);

        model.addAttribute("product", product); // product를 모델에 추가
        return "product/product-detail_test";
    }
}
