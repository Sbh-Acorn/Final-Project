package com.example.Caltizm.Controller;

import com.example.Caltizm.DTO.CartDTO;
import com.example.Caltizm.DTO.ProductDTO;
import com.example.Caltizm.Repository.DataRepository;
import com.example.Caltizm.Service.CalculatorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    DataRepository repository;

    @Autowired
    CalculatorService calculatorService;

    // 모든 메서드에서 사용할 제품 리스트를 미리 로드
    @ModelAttribute("products")
    public List<ProductDTO> getAllProducts() {
        List<ProductDTO> products = repository.getProduct();
        products.sort(Comparator.comparing(ProductDTO::getBrand));
        for (ProductDTO product : products){
            product.setCurrent_price(calculatorService.calculator(product.getCurrent_price()));
            if(product.getOriginal_price() != null){
                product.setOriginal_price(calculatorService.calculator(product.getOriginal_price()));
            }
        }
        return products;
    }

    @ModelAttribute("brandNames")
    public List<String> getAllBrandName(){
        return repository.getAllBrandName();
    }

    @ModelAttribute("categoryNames")
    public List<String> getAllCategoryName(){
        return repository.getAllCategoryName();
    }




    @GetMapping("/product")
    public String product(Model model) {
        return "product/product-list";  // products는 이미 Model에 추가됨
    }


    @GetMapping("/product/{product_id}")
    public String productDetail(@PathVariable(name = "product_id") String product_id, Model model , HttpSession session) {
        // Model에서 products를 가져오고 List로 캐스팅
        List<ProductDTO> products = (List<ProductDTO>) model.getAttribute("products");

        // itemCode에 해당하는 Product 찾기
        ProductDTO product = products.stream()
                .filter(p -> p.getProduct_id().equals(product_id))
                .findFirst()
                .orElse(null);

        model.addAttribute("product", product); // product를 모델에 추가

        if (product.getOriginal_price() != null) {
            int discountRate = (int) Math.round((1 - ((double) product.getCurrent_price() / product.getOriginal_price())) * 100);
            model.addAttribute("discountRate", discountRate);
        }
        // 세션에서 cartList 가져오기
        List<CartDTO> cartList = (List<CartDTO>) session.getAttribute("cartList");

        // cartList가 null이면 빈 리스트로 초기화
        if (cartList == null) {
            cartList = new ArrayList<>();
        }

        // Model에 cartList 추가
        model.addAttribute("cartList", cartList);
        return "product/product-detail";
    }
}
